package frc.robot.subsystems

import edu.wpi.first.math.geometry.Pose2d
import edu.wpi.first.math.geometry.Rotation2d
import edu.wpi.first.math.kinematics.ChassisSpeeds
import edu.wpi.first.math.kinematics.SwerveDriveKinematics
import edu.wpi.first.math.kinematics.SwerveDriveOdometry
import edu.wpi.first.math.kinematics.SwerveModulePosition
import edu.wpi.first.math.kinematics.SwerveModuleState
import edu.wpi.first.wpilibj.RobotBase
import edu.wpi.first.wpilibj2.command.SubsystemBase
import frc.robot.Constants

/**
 * Swerve drive subsystem with four modules.
 * Uses the IO pattern for hardware abstraction.
 */
class SwerveSubsystem : SubsystemBase() {

    // Swerve modules (front-left, front-right, back-left, back-right)
    private val modules: Array<SwerveModule>

    // Module IOs
    private val moduleIOs: Array<SwerveModuleIO>

    // Odometry for tracking robot position
    private val odometry: SwerveDriveOdometry
    private var gyroRotation = Rotation2d()

    companion object {
        // Motor type selection
        enum class MotorType {
            TALON_FX,    // Standard TalonFX (Falcon 500)
            KRAKEN_X60   // Kraken X60 (higher performance)
        }

        // Set this to your motor type
        val MOTOR_TYPE = MotorType.KRAKEN_X60

        // Module hardware IDs (adjust for your robot)
        // Format: [driveMotorId, turnMotorId, cancoderId]
        val MODULE_CONFIGS = arrayOf(
            Triple(1, 2, 1),  // Front Left
            Triple(3, 4, 2),  // Front Right
            Triple(5, 6, 3),  // Back Left
            Triple(7, 8, 4)   // Back Right
        )

        // CANcoder offsets (calibrate these for your robot)
        val CANCODER_OFFSETS = arrayOf(
            Rotation2d.fromDegrees(0.0),   // Front Left
            Rotation2d.fromDegrees(0.0),   // Front Right
            Rotation2d.fromDegrees(0.0),   // Back Left
            Rotation2d.fromDegrees(0.0)    // Back Right
        )
    }

    init {
        // Determine if we're in simulation
        val isSim = RobotBase.isSimulation()

        // Create module IOs based on environment and motor type
        moduleIOs = if (isSim) {
            Array(4) { SwerveModuleIOSim() }
        } else {
            when (MOTOR_TYPE) {
                MotorType.KRAKEN_X60 -> Array(4) { i ->
                    SwerveModuleIOKrakenX60(
                        driveMotorId = MODULE_CONFIGS[i].first,
                        turnMotorId = MODULE_CONFIGS[i].second,
                        cancoderId = MODULE_CONFIGS[i].third,
                        cancoderOffset = CANCODER_OFFSETS[i]
                    )
                }
                MotorType.TALON_FX -> Array(4) { i ->
                    SwerveModuleIOTalonFX(
                        driveMotorId = MODULE_CONFIGS[i].first,
                        turnMotorId = MODULE_CONFIGS[i].second,
                        cancoderId = MODULE_CONFIGS[i].third,
                        cancoderOffset = CANCODER_OFFSETS[i]
                    )
                }
            }
        }

        // Create modules
        modules = Array(4) { i ->
            SwerveModule(moduleIOs[i], "Module$i")
        }

        // Initialize odometry
        odometry = SwerveDriveOdometry(
            Constants.DriveConstants.SWERVE_KINEMATICS,
            gyroRotation,
            getModulePositions()
        )
    }

    override fun periodic() {
        // Update all modules
        modules.forEach { it.periodic() }

        // Update odometry
        odometry.update(gyroRotation, getModulePositions())
    }

    /**
     * Drive the robot with the given chassis speeds.
     */
    fun drive(chassisSpeeds: ChassisSpeeds) {
        // Convert chassis speeds to module states
        val moduleStates = Constants.DriveConstants.SWERVE_KINEMATICS.toSwerveModuleStates(chassisSpeeds)

        // Normalize wheel speeds
        SwerveDriveKinematics.desaturateWheelSpeeds(
            moduleStates,
            Constants.DriveConstants.MAX_SPEED_METERS_PER_SECOND
        )

        // Set module states
        setModuleStates(moduleStates)
    }

    /**
     * Sets the desired state for each swerve module.
     */
    fun setModuleStates(desiredStates: Array<SwerveModuleState>) {
        for (i in modules.indices) {
            modules[i].setDesiredState(desiredStates[i])
        }
    }

    /**
     * Gets the current module positions for odometry.
     */
    private fun getModulePositions(): Array<SwerveModulePosition> {
        return Array(4) { i ->
            SwerveModulePosition(
                modules[i].getDrivePositionMeters(),
                modules[i].getTurnPosition()
            )
        }
    }

    /**
     * Gets the current robot pose.
     */
    fun getPose(): Pose2d = odometry.poseMeters

    /**
     * Gets the current robot rotation.
     */
    fun getRotation(): Rotation2d = gyroRotation

    /**
     * Stops all swerve modules.
     */
    fun stop() {
        modules.forEach { it.stop() }
    }

    /**
     * Sets brake mode for all modules.
     */
    fun setBrakeMode(enable: Boolean) {
        modules.forEach { it.setBrakeMode(enable) }
    }
}

/**
 * Represents a single swerve module (one corner of the robot).
 */
class SwerveModule(
    private val io: SwerveModuleIO,
    private val name: String
) {
    private val inputs = SwerveModuleIO.SwerveModuleIOInputs()

    /**
     * Updates inputs from hardware.
     */
    fun periodic() {
        io.updateInputs(inputs)

        // TODO: Add AdvantageKit logging here
        // Logger.processInputs("Drive/$name", inputs)
    }

    /**
     * Sets the desired state for this module.
     */
    fun setDesiredState(desiredState: SwerveModuleState) {
        // Optimize the reference state to avoid spinning further than 90 degrees
        val optimizedState = SwerveModuleState.optimize(desiredState, inputs.turnPosition)

        // Simple open-loop control (voltage-based)
        // For production, use PID controllers for closed-loop control
        val driveVoltage = optimizedState.speedMetersPerSecond / Constants.DriveConstants.MAX_SPEED_METERS_PER_SECOND * 12.0

        // Simple proportional control for turn
        val turnError = optimizedState.angle.minus(inputs.turnPosition).radians
        val turnVoltage = turnError * 5.0 // Simple P gain

        io.setDriveVoltage(driveVoltage)
        io.setTurnVoltage(turnVoltage)
    }

    /**
     * Gets the current drive position in meters.
     */
    fun getDrivePositionMeters(): Double = inputs.drivePositionMeters

    /**
     * Gets the current drive velocity in meters per second.
     */
    fun getDriveVelocityMetersPerSec(): Double = inputs.driveVelocityMetersPerSec

    /**
     * Gets the current turn position.
     */
    fun getTurnPosition(): Rotation2d = inputs.turnPosition

    /**
     * Gets the absolute encoder position.
     */
    fun getAbsolutePosition(): Rotation2d = inputs.turnAbsolutePosition

    /**
     * Sets the drive motor voltage.
     */
    fun setDriveVoltage(volts: Double) {
        io.setDriveVoltage(volts)
    }

    /**
     * Sets the turn motor voltage.
     */
    fun setTurnVoltage(volts: Double) {
        io.setTurnVoltage(volts)
    }

    /**
     * Stops the module.
     */
    fun stop() {
        io.stop()
    }

    /**
     * Sets brake mode for both motors.
     */
    fun setBrakeMode(enable: Boolean) {
        io.setDriveBrakeMode(enable)
        io.setTurnBrakeMode(enable)
    }

    /**
     * Gets the odometry positions for high-frequency pose estimation.
     */
    fun getOdometryPositions(): Pair<DoubleArray, Array<Rotation2d>> {
        return Pair(inputs.odometryDrivePositionsMeters, inputs.odometryTurnPositions)
    }
}
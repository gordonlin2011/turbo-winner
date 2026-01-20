package frc.robot.subsystems

import edu.wpi.first.math.geometry.Rotation2d
import edu.wpi.first.wpilibj2.command.SubsystemBase

/**
 * Swerve drive subsystem with four modules.
 * Uses the IO pattern for hardware abstraction.
 */
class SwerveSubsystem : SubsystemBase() {

    // Swerve modules (front-left, front-right, back-left, back-right)
    private val modules: Array<SwerveModule>

    // Module IOs
    private val moduleIOs: Array<SwerveModuleIO>

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
        val isSim = false // TODO: Replace with Robot.isSimulation() or similar

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
    }

    override fun periodic() {
        // Update all modules
        modules.forEach { it.periodic() }
    }

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
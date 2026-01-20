package frc.robot.subsystems

import edu.wpi.first.math.MathUtil
import edu.wpi.first.math.geometry.Rotation2d
import edu.wpi.first.math.system.plant.DCMotor
import edu.wpi.first.math.system.plant.LinearSystemId
import edu.wpi.first.wpilibj.simulation.DCMotorSim

/**
 * Simulation implementation of SwerveModuleIO using WPILib physics simulation.
 */
class SwerveModuleIOSim : SwerveModuleIO {

    // Physics simulation
    private val driveSim = DCMotorSim(
        LinearSystemId.createDCMotorSystem(DCMotor.getFalcon500(1), 0.025, SwerveModuleIOTalonFX.DRIVE_GEAR_RATIO),
        DCMotor.getFalcon500(1)
    )

    private val turnSim = DCMotorSim(
        LinearSystemId.createDCMotorSystem(DCMotor.getFalcon500(1), 0.004, SwerveModuleIOTalonFX.TURN_GEAR_RATIO),
        DCMotor.getFalcon500(1)
    )

    // Simulated state
    private var driveAppliedVolts = 0.0
    private var turnAppliedVolts = 0.0

    private val turnAbsoluteInitPosition = Math.random() * 2.0 * Math.PI

    companion object {
        const val LOOP_PERIOD_SECS = 0.02
    }

    override fun updateInputs(inputs: SwerveModuleIO.SwerveModuleIOInputs) {
        // Update simulations
        driveSim.update(LOOP_PERIOD_SECS)
        turnSim.update(LOOP_PERIOD_SECS)

        // Update drive inputs
        inputs.drivePositionMeters =
            driveSim.angularPositionRad * SwerveModuleIOTalonFX.WHEEL_RADIUS_METERS
        inputs.driveVelocityMetersPerSec =
            driveSim.angularVelocityRadPerSec * SwerveModuleIOTalonFX.WHEEL_RADIUS_METERS
        inputs.driveAppliedVolts = driveAppliedVolts
        inputs.driveCurrentAmps = Math.abs(driveSim.currentDrawAmps)
        inputs.driveTempCelsius = 0.0

        // Update turn inputs
        inputs.turnAbsolutePosition = Rotation2d(turnSim.angularPositionRad + turnAbsoluteInitPosition)
        inputs.turnPosition = Rotation2d(turnSim.angularPositionRad)
        inputs.turnVelocityRadPerSec = turnSim.angularVelocityRadPerSec
        inputs.turnAppliedVolts = turnAppliedVolts
        inputs.turnCurrentAmps = Math.abs(turnSim.currentDrawAmps)
        inputs.turnTempCelsius = 0.0

        // Encoder always connected in sim
        inputs.turnEncoderConnected = true

        // Single odometry entry per update in sim
        inputs.odometryDrivePositionsMeters = doubleArrayOf(inputs.drivePositionMeters)
        inputs.odometryTurnPositions = arrayOf(inputs.turnPosition)
    }

    override fun setDriveVoltage(volts: Double) {
        driveAppliedVolts = MathUtil.clamp(volts, -12.0, 12.0)
        driveSim.setInputVoltage(driveAppliedVolts)
    }

    override fun setTurnVoltage(volts: Double) {
        turnAppliedVolts = MathUtil.clamp(volts, -12.0, 12.0)
        turnSim.setInputVoltage(turnAppliedVolts)
    }

    override fun setDriveBrakeMode(enable: Boolean) {
        // No-op in simulation
    }

    override fun setTurnBrakeMode(enable: Boolean) {
        // No-op in simulation
    }
}


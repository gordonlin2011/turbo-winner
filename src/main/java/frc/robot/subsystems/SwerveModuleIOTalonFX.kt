package frc.robot.subsystems

import com.ctre.phoenix6.BaseStatusSignal
import com.ctre.phoenix6.StatusSignal
import com.ctre.phoenix6.configs.CANcoderConfiguration
import com.ctre.phoenix6.configs.TalonFXConfiguration
import com.ctre.phoenix6.controls.VoltageOut
import com.ctre.phoenix6.hardware.CANcoder
import com.ctre.phoenix6.hardware.TalonFX
import com.ctre.phoenix6.signals.InvertedValue
import com.ctre.phoenix6.signals.NeutralModeValue
import edu.wpi.first.math.geometry.Rotation2d
import edu.wpi.first.math.util.Units

/**
 * Hardware implementation of SwerveModuleIO using CTRE TalonFX motors and CANcoder.
 */
class SwerveModuleIOTalonFX(
    private val driveMotorId: Int,
    private val turnMotorId: Int,
    private val cancoderId: Int,
    private val cancoderOffset: Rotation2d,
    private val driveInverted: Boolean = false,
    private val turnInverted: Boolean = false
) : SwerveModuleIO {

    private val driveMotor = TalonFX(driveMotorId)
    private val turnMotor = TalonFX(turnMotorId)
    private val cancoder = CANcoder(cancoderId)

    // Control requests
    private val driveVoltageRequest = VoltageOut(0.0)
    private val turnVoltageRequest = VoltageOut(0.0)

    // Status signals for drive motor
    private val drivePosition: StatusSignal<*>
    private val driveVelocity: StatusSignal<*>
    private val driveAppliedVolts: StatusSignal<*>
    private val driveCurrent: StatusSignal<*>
    private val driveTemp: StatusSignal<*>

    // Status signals for turn motor
    private val turnAbsolutePosition: StatusSignal<*>
    private val turnPosition: StatusSignal<*>
    private val turnVelocity: StatusSignal<*>
    private val turnAppliedVolts: StatusSignal<*>
    private val turnCurrent: StatusSignal<*>
    private val turnTemp: StatusSignal<*>

    // Constants (adjust these for your robot)
    companion object {
        const val DRIVE_GEAR_RATIO = 6.75 // L2 ratio
        const val TURN_GEAR_RATIO = 21.428571428571427 // 150.0 / 7.0
        val WHEEL_RADIUS_METERS = Units.inchesToMeters(2.0)
        const val ODOMETRY_FREQUENCY = 250.0 // Hz
    }

    init {
        // Configure drive motor
        val driveConfig = TalonFXConfiguration()
        driveConfig.CurrentLimits.SupplyCurrentLimit = 40.0
        driveConfig.CurrentLimits.SupplyCurrentLimitEnable = true
        driveConfig.MotorOutput.Inverted = if (driveInverted)
            InvertedValue.Clockwise_Positive else InvertedValue.CounterClockwise_Positive
        driveMotor.configurator.apply(driveConfig)

        // Configure turn motor
        val turnConfig = TalonFXConfiguration()
        turnConfig.CurrentLimits.SupplyCurrentLimit = 30.0
        turnConfig.CurrentLimits.SupplyCurrentLimitEnable = true
        turnConfig.MotorOutput.Inverted = if (turnInverted)
            InvertedValue.Clockwise_Positive else InvertedValue.CounterClockwise_Positive
        turnMotor.configurator.apply(turnConfig)

        // Configure CANcoder
        val cancoderConfig = CANcoderConfiguration()
        cancoder.configurator.apply(cancoderConfig)

        // Get status signals
        drivePosition = driveMotor.position
        driveVelocity = driveMotor.velocity
        driveAppliedVolts = driveMotor.motorVoltage
        driveCurrent = driveMotor.supplyCurrent
        driveTemp = driveMotor.deviceTemp

        turnAbsolutePosition = cancoder.absolutePosition
        turnPosition = turnMotor.position
        turnVelocity = turnMotor.velocity
        turnAppliedVolts = turnMotor.motorVoltage
        turnCurrent = turnMotor.supplyCurrent
        turnTemp = turnMotor.deviceTemp

        // Set update frequencies
        BaseStatusSignal.setUpdateFrequencyForAll(
            100.0,
            driveAppliedVolts,
            driveCurrent,
            driveTemp,
            turnAppliedVolts,
            turnCurrent,
            turnTemp
        )

        BaseStatusSignal.setUpdateFrequencyForAll(
            ODOMETRY_FREQUENCY,
            drivePosition,
            driveVelocity,
            turnAbsolutePosition,
            turnPosition,
            turnVelocity
        )

        // Optimize bus utilization
        driveMotor.optimizeBusUtilization()
        turnMotor.optimizeBusUtilization()
        cancoder.optimizeBusUtilization()
    }

    override fun updateInputs(inputs: SwerveModuleIO.SwerveModuleIOInputs) {
        // Refresh all signals
        BaseStatusSignal.refreshAll(
            drivePosition,
            driveVelocity,
            driveAppliedVolts,
            driveCurrent,
            driveTemp,
            turnAbsolutePosition,
            turnPosition,
            turnVelocity,
            turnAppliedVolts,
            turnCurrent,
            turnTemp
        )

        // Update drive inputs
        inputs.drivePositionMeters =
            drivePosition.valueAsDouble / DRIVE_GEAR_RATIO * 2.0 * Math.PI * WHEEL_RADIUS_METERS
        inputs.driveVelocityMetersPerSec =
            driveVelocity.valueAsDouble / DRIVE_GEAR_RATIO * 2.0 * Math.PI * WHEEL_RADIUS_METERS
        inputs.driveAppliedVolts = driveAppliedVolts.valueAsDouble
        inputs.driveCurrentAmps = driveCurrent.valueAsDouble
        inputs.driveTempCelsius = driveTemp.valueAsDouble

        // Update turn inputs
        inputs.turnAbsolutePosition =
            Rotation2d.fromRotations(turnAbsolutePosition.valueAsDouble).minus(cancoderOffset)
        inputs.turnPosition = Rotation2d.fromRotations(turnPosition.valueAsDouble / TURN_GEAR_RATIO)
        inputs.turnVelocityRadPerSec =
            Units.rotationsToRadians(turnVelocity.valueAsDouble) / TURN_GEAR_RATIO
        inputs.turnAppliedVolts = turnAppliedVolts.valueAsDouble
        inputs.turnCurrentAmps = turnCurrent.valueAsDouble
        inputs.turnTempCelsius = turnTemp.valueAsDouble

        // Update encoder status
        inputs.turnEncoderConnected = turnAbsolutePosition.status.isOK

        // Get odometry data (high-frequency position updates)
        // Note: Odometry queues require Phoenix Pro for queue collection
        // For now, use single samples
        inputs.odometryDrivePositionsMeters = doubleArrayOf(inputs.drivePositionMeters)
        inputs.odometryTurnPositions = arrayOf(inputs.turnPosition)
    }

    override fun setDriveVoltage(volts: Double) {
        driveMotor.setControl(driveVoltageRequest.withOutput(volts))
    }

    override fun setTurnVoltage(volts: Double) {
        turnMotor.setControl(turnVoltageRequest.withOutput(volts))
    }

    override fun setDriveBrakeMode(enable: Boolean) {
        val config = TalonFXConfiguration()
        config.MotorOutput.NeutralMode = if (enable)
            NeutralModeValue.Brake else NeutralModeValue.Coast
        driveMotor.configurator.apply(config)
    }

    override fun setTurnBrakeMode(enable: Boolean) {
        val config = TalonFXConfiguration()
        config.MotorOutput.NeutralMode = if (enable)
            NeutralModeValue.Brake else NeutralModeValue.Coast
        turnMotor.configurator.apply(config)
    }
}

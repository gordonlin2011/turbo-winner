package frc.robot.subsystems

import edu.wpi.first.math.geometry.Rotation2d

/**
 * IO interface for a single swerve module.
 * Follows AdvantageKit's IO pattern for hardware abstraction.
 */
interface SwerveModuleIO {
    /**
     * Input class containing all sensor readings and state from the swerve module.
     */
    data class SwerveModuleIOInputs(
        // Drive motor inputs
        var drivePositionMeters: Double = 0.0,
        var driveVelocityMetersPerSec: Double = 0.0,
        var driveAppliedVolts: Double = 0.0,
        var driveCurrentAmps: Double = 0.0,
        var driveTempCelsius: Double = 0.0,

        // Turn motor inputs
        var turnAbsolutePosition: Rotation2d = Rotation2d(),
        var turnPosition: Rotation2d = Rotation2d(),
        var turnVelocityRadPerSec: Double = 0.0,
        var turnAppliedVolts: Double = 0.0,
        var turnCurrentAmps: Double = 0.0,
        var turnTempCelsius: Double = 0.0,

        // Encoder inputs
        var turnEncoderConnected: Boolean = true,

        // Odometry (for high-frequency updates)
        var odometryDrivePositionsMeters: DoubleArray = doubleArrayOf(),
        var odometryTurnPositions: Array<Rotation2d> = arrayOf()
    ) {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as SwerveModuleIOInputs

            if (drivePositionMeters != other.drivePositionMeters) return false
            if (driveVelocityMetersPerSec != other.driveVelocityMetersPerSec) return false
            if (driveAppliedVolts != other.driveAppliedVolts) return false
            if (driveCurrentAmps != other.driveCurrentAmps) return false
            if (driveTempCelsius != other.driveTempCelsius) return false
            if (turnVelocityRadPerSec != other.turnVelocityRadPerSec) return false
            if (turnAppliedVolts != other.turnAppliedVolts) return false
            if (turnCurrentAmps != other.turnCurrentAmps) return false
            if (turnTempCelsius != other.turnTempCelsius) return false
            if (turnEncoderConnected != other.turnEncoderConnected) return false
            if (turnAbsolutePosition != other.turnAbsolutePosition) return false
            if (turnPosition != other.turnPosition) return false
            if (!odometryDrivePositionsMeters.contentEquals(other.odometryDrivePositionsMeters)) return false
            if (!odometryTurnPositions.contentEquals(other.odometryTurnPositions)) return false

            return true
        }

        override fun hashCode(): Int {
            var result = drivePositionMeters.hashCode()
            result = 31 * result + driveVelocityMetersPerSec.hashCode()
            result = 31 * result + driveAppliedVolts.hashCode()
            result = 31 * result + driveCurrentAmps.hashCode()
            result = 31 * result + driveTempCelsius.hashCode()
            result = 31 * result + turnVelocityRadPerSec.hashCode()
            result = 31 * result + turnAppliedVolts.hashCode()
            result = 31 * result + turnCurrentAmps.hashCode()
            result = 31 * result + turnTempCelsius.hashCode()
            result = 31 * result + turnEncoderConnected.hashCode()
            result = 31 * result + turnAbsolutePosition.hashCode()
            result = 31 * result + turnPosition.hashCode()
            result = 31 * result + odometryDrivePositionsMeters.contentHashCode()
            result = 31 * result + odometryTurnPositions.contentHashCode()
            return result
        }
    }

    /**
     * Updates the set of loggable inputs.
     * Should be called periodically (usually in periodic()).
     */
    fun updateInputs(inputs: SwerveModuleIOInputs)

    /**
     * Sets the desired drive voltage.
     * @param volts Voltage to apply to drive motor (-12.0 to 12.0)
     */
    fun setDriveVoltage(volts: Double)

    /**
     * Sets the desired turn voltage.
     * @param volts Voltage to apply to turn motor (-12.0 to 12.0)
     */
    fun setTurnVoltage(volts: Double)

    /**
     * Sets the drive motor to brake or coast mode.
     * @param enable True for brake mode, false for coast mode
     */
    fun setDriveBrakeMode(enable: Boolean)

    /**
     * Sets the turn motor to brake or coast mode.
     * @param enable True for brake mode, false for coast mode
     */
    fun setTurnBrakeMode(enable: Boolean)

    /**
     * Stops both motors.
     */
    fun stop() {
        setDriveVoltage(0.0)
        setTurnVoltage(0.0)
    }

    /**
     * Resets the drive encoder position to zero.
     */
    fun resetDriveEncoder() {
        // Default implementation does nothing
    }

    /**
     * Syncs the turn motor encoder to the absolute CANcoder position.
     * This should be called on robot init to ensure all wheels start at the correct angle.
     */
    fun syncTurnEncoderToAbsolute() {
        // Default implementation does nothing
    }
}

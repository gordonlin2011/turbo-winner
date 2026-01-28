package frc.robot.commands

import edu.wpi.first.math.MathUtil
import edu.wpi.first.math.kinematics.ChassisSpeeds
import edu.wpi.first.wpilibj2.command.Command
import frc.robot.Constants.DriveConstants
import frc.robot.subsystems.SwerveSubsystem
import java.util.function.DoubleSupplier

/**
 * Command to drive the swerve robot using joystick inputs.
 * Uses field-relative control by default.
 */
class DriveCommand(
    private val swerve: SwerveSubsystem,
    private val xSupplier: DoubleSupplier,
    private val ySupplier: DoubleSupplier,
    private val rotSupplier: DoubleSupplier,
    private val fieldRelative: Boolean = true
) : Command() {

    init {
        addRequirements(swerve)
    }

    override fun execute() {
        // Get joystick inputs with deadband
        val xSpeed = MathUtil.applyDeadband(xSupplier.asDouble, DriveConstants.JOYSTICK_DEADBAND)
        val ySpeed = MathUtil.applyDeadband(ySupplier.asDouble, DriveConstants.JOYSTICK_DEADBAND)
        val rot = MathUtil.applyDeadband(rotSupplier.asDouble, DriveConstants.JOYSTICK_DEADBAND)

        // Convert to speeds (apply max speed scaling)
        val xSpeedMeters = xSpeed * DriveConstants.MAX_SPEED_METERS_PER_SECOND
        val ySpeedMeters = ySpeed * DriveConstants.MAX_SPEED_METERS_PER_SECOND
        val rotRadians = rot * DriveConstants.MAX_ANGULAR_SPEED_RADIANS_PER_SECOND

        // Create chassis speeds
        val chassisSpeeds = if (fieldRelative) {
            // Field-relative control
            ChassisSpeeds.fromFieldRelativeSpeeds(
                xSpeedMeters,
                ySpeedMeters,
                rotRadians,
                swerve.getRotation()
            )
        } else {
            // Robot-relative control
            ChassisSpeeds(xSpeedMeters, ySpeedMeters, rotRadians)
        }

        // Command the swerve subsystem
        swerve.drive(chassisSpeeds)
    }

    override fun end(interrupted: Boolean) {
        swerve.stop()
    }

    override fun isFinished(): Boolean = false
}

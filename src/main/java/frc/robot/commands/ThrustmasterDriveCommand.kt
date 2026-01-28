package frc.robot.commands

import edu.wpi.first.math.MathUtil
import edu.wpi.first.math.kinematics.ChassisSpeeds
import edu.wpi.first.wpilibj2.command.Command
import edu.wpi.first.wpilibj2.command.button.CommandJoystick
import frc.robot.Constants.DriveConstants
import frc.robot.Constants.OIConstants
import frc.robot.subsystems.SwerveSubsystem

/**
 * Advanced drive command specifically designed for Thrustmaster T.16000M FCS joystick.
 * Features:
 * - Throttle-based speed control
 * - Precision and turbo modes
 * - Field/robot relative toggle
 * - Rotation lock
 */
class ThrustmasterDriveCommand(
    private val swerve: SwerveSubsystem,
    private val joystick: CommandJoystick
) : Command() {

    private var fieldRelative = true
    private var speedMultiplier = OIConstants.NORMAL_MULTIPLIER
    private var rotationLocked = false

    init {
        addRequirements(swerve)
    }

    override fun execute() {
        // Get raw joystick inputs
        val rawX = joystick.x
        val rawY = joystick.y
        val rawTwist = joystick.twist
        val rawThrottle = joystick.getRawAxis(OIConstants.JOYSTICK_THROTTLE_AXIS)

        // Apply deadbands
        val xInput = MathUtil.applyDeadband(rawX, OIConstants.AXIS_DEADBAND)
        val yInput = MathUtil.applyDeadband(rawY, OIConstants.AXIS_DEADBAND)
        val twistInput = MathUtil.applyDeadband(rawTwist, OIConstants.TWIST_DEADBAND)

        // Convert throttle from -1..1 to 0.2..1.0 (20% minimum, 100% maximum)
        val throttle = MathUtil.interpolate(
            OIConstants.THROTTLE_MIN,
            OIConstants.THROTTLE_MAX,
            (rawThrottle + 1.0) / 2.0  // Convert -1..1 to 0..1
        )

        // Determine speed mode (precision or turbo overrides throttle)
        val activeMultiplier = when {
            joystick.button(OIConstants.BUTTON_PRECISION).asBoolean -> OIConstants.PRECISION_MULTIPLIER
            joystick.button(OIConstants.BUTTON_TURBO).asBoolean -> OIConstants.TURBO_MULTIPLIER
            else -> throttle
        }

        // Calculate speeds with throttle and mode multiplier
        val xSpeed = -yInput * activeMultiplier  // Inverted: forward is negative on joystick
        val ySpeed = -xInput * activeMultiplier  // Inverted: left is negative on joystick

        // Rotation lock feature
        val rotSpeed = if (rotationLocked) {
            0.0
        } else {
            -twistInput * activeMultiplier  // Inverted
        }

        // Convert to robot speeds
        val xSpeedMeters = xSpeed * DriveConstants.MAX_SPEED_METERS_PER_SECOND
        val ySpeedMeters = ySpeed * DriveConstants.MAX_SPEED_METERS_PER_SECOND
        val rotRadians = rotSpeed * DriveConstants.MAX_ANGULAR_SPEED_RADIANS_PER_SECOND

        // Create chassis speeds
        val chassisSpeeds = if (fieldRelative) {
            ChassisSpeeds.fromFieldRelativeSpeeds(
                xSpeedMeters,
                ySpeedMeters,
                rotRadians,
                swerve.getRotation()
            )
        } else {
            ChassisSpeeds(xSpeedMeters, ySpeedMeters, rotRadians)
        }

        // Command the swerve subsystem
        swerve.drive(chassisSpeeds)
    }

    override fun end(interrupted: Boolean) {
        swerve.stop()
    }

    override fun isFinished(): Boolean = false

    /**
     * Toggle between field-relative and robot-relative control.
     */
    fun toggleFieldRelative() {
        fieldRelative = !fieldRelative
    }

    /**
     * Set field-relative mode.
     */
    fun setFieldRelative(enabled: Boolean) {
        fieldRelative = enabled
    }

    /**
     * Toggle rotation lock (prevents robot from rotating).
     */
    fun toggleRotationLock() {
        rotationLocked = !rotationLocked
    }

    /**
     * Set rotation lock.
     */
    fun setRotationLock(locked: Boolean) {
        rotationLocked = locked
    }

    /**
     * Get current field-relative state.
     */
    fun isFieldRelative(): Boolean = fieldRelative

    /**
     * Get current rotation lock state.
     */
    fun isRotationLocked(): Boolean = rotationLocked
}

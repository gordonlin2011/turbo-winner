package frc.robot

import edu.wpi.first.wpilibj2.command.Command
import edu.wpi.first.wpilibj2.command.button.CommandJoystick
import frc.robot.commands.DriveCommand
import frc.robot.subsystems.SwerveSubsystem

/**
 * This class is where the bulk of the robot should be declared. Since Command-based is a
 * "declarative" paradigm, very little robot logic should actually be handled in the [Robot]
 * periodic methods (other than the scheduler calls). Instead, the structure of the robot (including
 * subsystems, commands, and trigger mappings) should be declared here.
 */
object RobotContainer
{
    // Subsystems
    val swerveSubsystem = SwerveSubsystem()

    // Controllers - Thrustmaster 16000M Joystick
    private val driverJoystick = CommandJoystick(0)

    init
    {
        configureBindings()
        configureDefaultCommands()
    }

    /**
     * Configure button bindings for teleop control.
     * Thrustmaster 16000M has buttons numbered 1-16 on base.
     */
    private fun configureBindings()
    {
        // Button 1 (trigger) - Set brake mode
        driverJoystick.button(1).onTrue(
            swerveSubsystem.runOnce { swerveSubsystem.setBrakeMode(true) }
        )

        // Button 2 (thumb button) - Set coast mode
        driverJoystick.button(2).onTrue(
            swerveSubsystem.runOnce { swerveSubsystem.setBrakeMode(false) }
        )

        // Button 3 - Stop all modules
        driverJoystick.button(3).onTrue(
            swerveSubsystem.runOnce { swerveSubsystem.stop() }
        )
    }

    /**
     * Set up default commands for subsystems.
     */
    private fun configureDefaultCommands()
    {
        // Default command: Drive with joysticks
        swerveSubsystem.defaultCommand = getDriveCommand()
    }

    /**
     * Gets the main drive command using joystick inputs.
     * Thrustmaster 16000M: X/Y for translation, Twist (Z-axis) for rotation.
     */
    private fun getDriveCommand(): Command {
        return DriveCommand(
            swerveSubsystem,
            { -driverJoystick.y },     // Forward/backward (inverted)
            { -driverJoystick.x },     // Left/right (inverted)
            { -driverJoystick.twist }, // Rotation from twist axis (inverted)
            true                       // Field-relative
        )
    }
}
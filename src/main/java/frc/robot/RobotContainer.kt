package frc.robot

import edu.wpi.first.wpilibj2.command.Command
import edu.wpi.first.wpilibj2.command.button.CommandXboxController
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

    // Controllers
    private val driverController = CommandXboxController(0)

    init
    {
        configureBindings()
        configureDefaultCommands()
    }

    /**
     * Configure button bindings for teleop control.
     */
    private fun configureBindings()
    {
        // X button - Set brake mode
        driverController.x().onTrue(
            swerveSubsystem.runOnce { swerveSubsystem.setBrakeMode(true) }
        )

        // B button - Set coast mode
        driverController.b().onTrue(
            swerveSubsystem.runOnce { swerveSubsystem.setBrakeMode(false) }
        )

        // A button - Stop all modules
        driverController.a().onTrue(
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
     */
    private fun getDriveCommand(): Command {
        return DriveCommand(
            swerveSubsystem,
            { -driverController.leftY },  // Forward/backward (inverted)
            { -driverController.leftX },  // Left/right (inverted)
            { -driverController.rightX }, // Rotation (inverted)
            true                          // Field-relative
        )
    }
}
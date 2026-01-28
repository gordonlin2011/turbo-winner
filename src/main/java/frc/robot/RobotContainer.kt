package frc.robot

import edu.wpi.first.wpilibj2.command.Command
import edu.wpi.first.wpilibj2.command.button.CommandJoystick
import frc.robot.Constants.OIConstants
import frc.robot.commands.ThrustmasterDriveCommand
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

    // Controllers - Thrustmaster T.16000M FCS Joystick
    private val driverJoystick = CommandJoystick(OIConstants.DRIVER_JOYSTICK_PORT)

    // Main drive command
    private val driveCommand = ThrustmasterDriveCommand(swerveSubsystem, driverJoystick)

    init
    {
        configureBindings()
        configureDefaultCommands()
    }

    /**
     * Configure button bindings for teleop control.
     * See THRUSTMASTER_CONTROL_SCHEME.md for complete control layout.
     */
    private fun configureBindings()
    {
        // ===== CRITICAL CONTROLS =====

        // Button 1 (Trigger) - Enable brake mode
        driverJoystick.button(OIConstants.BUTTON_TRIGGER).onTrue(
            swerveSubsystem.runOnce { swerveSubsystem.setBrakeMode(true) }
        )

        // Button 2 (Thumb button) - Enable coast mode
        driverJoystick.button(OIConstants.BUTTON_THUMB).onTrue(
            swerveSubsystem.runOnce { swerveSubsystem.setBrakeMode(false) }
        )

        // Button 3 (Top left) - Emergency stop
        driverJoystick.button(OIConstants.BUTTON_TOP_LEFT).onTrue(
            swerveSubsystem.runOnce { swerveSubsystem.stop() }
        )

        // Button 4 (Top right) - Toggle field/robot relative
        driverJoystick.button(OIConstants.BUTTON_TOP_RIGHT).onTrue(
            swerveSubsystem.runOnce { driveCommand.toggleFieldRelative() }
        )

        // ===== DRIVE MODE CONTROLS =====

        // Button 5 - Precision mode (held during button press)
        // Implemented directly in ThrustmasterDriveCommand

        // Button 6 - Turbo mode (held during button press)
        // Implemented directly in ThrustmasterDriveCommand

        // Button 7 - Auto-balance (placeholder for future implementation)
        driverJoystick.button(OIConstants.BUTTON_AUTO_BALANCE).whileTrue(
            swerveSubsystem.run {
                // TODO: Implement auto-balance command
                // For now, just runs the subsystem
            }
        )

        // Button 8 - Lock rotation (toggle)
        driverJoystick.button(OIConstants.BUTTON_LOCK_ROTATION).onTrue(
            swerveSubsystem.runOnce { driveCommand.toggleRotationLock() }
        )

        // ===== MECHANISM CONTROLS (Placeholder for future subsystems) =====

        // Button 9 - Intake deploy
        driverJoystick.button(OIConstants.BUTTON_INTAKE_DEPLOY).onTrue(
            swerveSubsystem.runOnce {
                // TODO: Implement intake subsystem
                // intakeSubsystem.deploy()
            }
        )

        // Button 10 - Intake run
        driverJoystick.button(OIConstants.BUTTON_INTAKE_RUN).whileTrue(
            swerveSubsystem.run {
                // TODO: Implement intake subsystem
                // intakeSubsystem.runIntake()
            }
        )

        // Button 11 - Intake reverse
        driverJoystick.button(OIConstants.BUTTON_INTAKE_REVERSE).whileTrue(
            swerveSubsystem.run {
                // TODO: Implement intake subsystem
                // intakeSubsystem.reverseIntake()
            }
        )

        // Button 12 - Intake retract
        driverJoystick.button(OIConstants.BUTTON_INTAKE_RETRACT).onTrue(
            swerveSubsystem.runOnce {
                // TODO: Implement intake subsystem
                // intakeSubsystem.retract()
            }
        )

        // Button 13 - Shooter spin up
        driverJoystick.button(OIConstants.BUTTON_SHOOTER_SPINUP).whileTrue(
            swerveSubsystem.run {
                // TODO: Implement shooter subsystem
                // shooterSubsystem.spinUp()
            }
        )

        // Button 14 - Shooter fire
        driverJoystick.button(OIConstants.BUTTON_SHOOTER_FIRE).onTrue(
            swerveSubsystem.runOnce {
                // TODO: Implement shooter subsystem
                // shooterSubsystem.fire()
            }
        )

        // Button 15 - Climber extend
        driverJoystick.button(OIConstants.BUTTON_CLIMBER_EXTEND).onTrue(
            swerveSubsystem.runOnce {
                // TODO: Implement climber subsystem
                // climberSubsystem.extend()
            }
        )

        // Button 16 - Climber retract
        driverJoystick.button(OIConstants.BUTTON_CLIMBER_RETRACT).onTrue(
            swerveSubsystem.runOnce {
                // TODO: Implement climber subsystem
                // climberSubsystem.retract()
            }
        )

        // ===== POV HAT CONTROLS (Placeholder for arm/mechanism control) =====

        // POV Up - Move arm up
        driverJoystick.pov(OIConstants.POV_UP).whileTrue(
            swerveSubsystem.run {
                // TODO: Implement arm subsystem
                // armSubsystem.moveUp()
            }
        )

        // POV Down - Move arm down
        driverJoystick.pov(OIConstants.POV_DOWN).whileTrue(
            swerveSubsystem.run {
                // TODO: Implement arm subsystem
                // armSubsystem.moveDown()
            }
        )

        // POV Left - Move arm left
        driverJoystick.pov(OIConstants.POV_LEFT).whileTrue(
            swerveSubsystem.run {
                // TODO: Implement arm subsystem
                // armSubsystem.moveLeft()
            }
        )

        // POV Right - Move arm right
        driverJoystick.pov(OIConstants.POV_RIGHT).whileTrue(
            swerveSubsystem.run {
                // TODO: Implement arm subsystem
                // armSubsystem.moveRight()
            }
        )

        // POV Diagonals - Preset positions
        driverJoystick.pov(OIConstants.POV_UP_RIGHT).onTrue(
            swerveSubsystem.runOnce {
                // TODO: armSubsystem.setPresetHigh()
            }
        )

        driverJoystick.pov(OIConstants.POV_UP_LEFT).onTrue(
            swerveSubsystem.runOnce {
                // TODO: armSubsystem.setPresetMid()
            }
        )

        driverJoystick.pov(OIConstants.POV_DOWN_RIGHT).onTrue(
            swerveSubsystem.runOnce {
                // TODO: armSubsystem.setPresetLow()
            }
        )

        driverJoystick.pov(OIConstants.POV_DOWN_LEFT).onTrue(
            swerveSubsystem.runOnce {
                // TODO: armSubsystem.setPresetGround()
            }
        )
    }

    /**
     * Set up default commands for subsystems.
     */
    private fun configureDefaultCommands()
    {
        // Default command: Advanced Thrustmaster drive with throttle control
        swerveSubsystem.defaultCommand = driveCommand
    }
}
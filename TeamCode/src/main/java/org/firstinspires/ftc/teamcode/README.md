# MPHS Robotics — FTC Team 17190
This repository contains the Java code for MPHS Robotics Team 17190's FIRST Tech
Challenge robot.
The project uses the FTC Robot Controller SDK for the 2026–2027 BIOBUZZ season and
includes mecanum drive control, three-wheel odometry, PID-based movement, and early turret
and intake implementations.
## Project Structure
```text
MPHS-Robotics-Ftc-Team17190/
├── FtcRobotController/
│ └── FTC Robot Controller app and official SDK samples
│
├── TeamCode/
│ └── src/main/java/org/firstinspires/ftc/teamcode/
│ ├── AdvancedMecanum.java
│ ├── BasicMecanum.java
│ ├── TestOdo.java
│ ├── TestPIDRemoveLater.java
│ ├── README.md
│ │
│ └── Helpers/
│ ├── Intake.java
│ ├── Odometry.java
│ ├── PID.java
│ ├── RobotDrive.java
│ └── Turret.java
│
└── Gradle Scripts
```
## Robot Programs
### `BasicMecanum.java`
**Type:** TeleOp
**Drive:** Robot-centric mecanum
Provides basic manual control of the robot using the first gamepad. The left stick controls
translation, while the right stick's horizontal axis contributes to rotation.
The program calculates individual wheel powers and sends them to the four drive motors. It also
reports controller inputs and wheel powers through telemetry.
### `AdvancedMecanum.java`
**Type:** TeleOp
**Drive:** Field-centric mecanum
**Additional systems:** Odometry and HuskyLens
Extends the basic driving approach with field-centric movement, allowing the driver's translation
inputs to be transformed using the robot's estimated heading.
The program also:
- Updates the odometry system and reports robot position.
- Displays raw encoder readings and heading sanity checks.
- Initializes the turret helper and calls its auto-aim routine.
- Calls the shooting routine when the right trigger is pressed.
  **Implementation status:** Field-centric drive and odometry calculations are present. Turret
  movement and the actual shooting sequence are not yet implemented in `Turret.java`.
### `TestOdo.java`
**Type:** TeleOp
**Status:** Disabled
A testing program for validating three-wheel odometry. It displays encoder ticks, estimated
distances, robot position, and heading.
Pressing the A button resets the encoder readings and odometry origin. The program also
calculates heading independently of the raw encoder distances so the result can be compared
against the odometry estimate.
Because the class is annotated with `@Disabled`, it will not appear as an available OpMode
until that annotation is removed.
### `TestPIDRemoveLater.java`
**Type:** Autonomous
**Status:** Disabled
A test program for evaluating PID-based robot movement toward a target position and heading.
The current target is configured as:
```java
double[] target = {0, 0, Math.toRadians(30)};
```
The program reports the robot's estimated position, motor powers, and derivative terms.
This is a development/testing OpMode, not a completed competition autonomous routine.
## Helper Classes
The helper classes separate robot calculations and subsystem logic from the OpModes that use
them.
### `Odometry.java`
Estimates the robot's position on the field using three encoder measurements:
- Front-right drive motor: right parallel tracking wheel.
- Front-left drive motor: left parallel tracking wheel.
- Back-right drive motor: perpendicular tracking wheel.
  The encoder measurements are converted into distances, which are used to calculate changes
  in heading and robot-relative movement. Those changes are then transformed into field
  coordinates.
  The system tracks:
  | Value | Description | Unit |
  |-----------|------------------------------------|---------|
  | `X` | Field-relative horizontal position | mm |
  | `Y` | Field-relative vertical position | mm |
  | `Heading` | Robot orientation | radians |
  Current odometry constants:
  | Constant | Value | Unit |
  |-----------------------|-------:|------------------|
  | `wheelDiameter` | 32.0 | mm |
  | `tickPerRev` | 2000.0 | ticks/revolution |
  | `trackWidth` | 138.6 | mm |
  | `perpendicularOffset` | 228.10 | mm |
  These values must match the actual tracking-wheel hardware and geometry. They should be
  calibrated and verified on the physical robot.
  Main methods:
- `evalPos()` — updates and returns the estimated robot position.
- `reset()` — resets the three encoder measurements and position estimate.
- `getX()` — returns the current X position.
- `getY()` — returns the current Y position.
- `getHeading()` — returns the current heading.
- `getRobotPosition()` — returns `{X, Y, Heading}`.
  **Important:** The current position and encoder-history values are static. Multiple `Odometry`
  instances therefore share state rather than maintaining independent position estimates.
### `PID.java`
Calculates mecanum motor powers to move the robot toward a target position and heading.
The controller:
- Calculates position and heading error.
- Wraps heading error into the range `-π` to `π`.
- Transforms field-relative position error into robot-relative coordinates.
- Applies proportional, integral, and derivative terms.
- Limits changes in commanded power to control acceleration.
- Converts the resulting translation and rotation commands into four motor powers.
  The constructor accepts the translation PID gains:
```java
PID pid = new PID(kP, kI, kD);
```
Heading control has separate proportional and derivative gains defined inside the class.
Main methods:
- `updateRobot(robotPosition, target)` — calculates and returns four motor powers.
- `resetTime()` — resets timing, command history, and integral state.
  The controller is used by `RobotDrive.java` and the disabled PID test OpMode. Its behavior and
  gains still require physical testing and tuning.
### `RobotDrive.java`
Provides a higher-level interface for autonomous-style movement.
Its constructor accepts telemetry, the hardware map, and an array of target points. It initializes
the drive motors, odometry, PID controller, turret helper, and intake helper.
The class:
- Tracks progress through target points.
- Checks position and heading tolerances.
- Uses PID control to calculate drive motor powers.
- Can hold at a target while an action is processed.
- Includes hooks for shooting and intake actions.
- Returns zero motor powers when all target points are complete.
  Target-point data is currently interpreted in this format:
```text
{X, Y, Heading, Precision, Action}
```
| Field | Description |
|-------------|---------------------------------------------|
| `X` | Target field X position in mm |
| `Y` | Target field Y position in mm |
| `Heading` | Target heading in radians |
| `Precision` | Position and heading tolerance setting |
| `Action` | Action code used when the target is reached |
The current action handling uses these values:
| Action value | Behavior |
|-------------:|---------------------------------------------------|
| `0` | Continue to the next target without a hold action |
| `2` | Hold while the turret's shooting routine runs |
| `3` | Hold while the intake routine runs |
Values above `1` trigger the hold-point logic. The behavior of other values is not fully defined.
**Implementation status:** `RobotDrive` is a helper class, not an OpMode. The uploaded project
does not currently contain an `AutoStateMachine.java` or another active autonomous OpMode
that uses `RobotDrive` to run a complete target sequence.
### `Turret.java`
Contains the current turret, target-recognition, and shooting logic.
The class initializes a HuskyLens configured for tag recognition and reads detected blocks. It
maps HuskyLens IDs `1–8` to IDs `30–37` and classifies those IDs into `LEFT_UP`,
`RIGHT_UP`, or `UNKNOWN`.
The auto-aim routine also calculates a desired target direction from the robot's estimated field
position and heading.
**Current limitations:**
- `autoAim()` calculates aiming information and reports detected tags, but does not command
  turret hardware to rotate.
- `shoot()` is a placeholder that immediately returns `true`; no shooting sequence is
  implemented.
- No turret motor or shooter motor is currently initialized in this class.
### `Intake.java`
Contains an early intake implementation with methods for starting intake behavior, checking the
number of collected game pieces, and deciding whether more pieces can be collected.
**Current limitations:**
- The class uses `front_left_drive` as its motor instead of a dedicated intake motor.
- `checkBalls()` generates a random count rather than reading a sensor.
- The intake logic is therefore a placeholder and should not be treated as a finished subsystem.
## Hardware Configuration
The code expects the following configured hardware names:
| Device | Hardware configuration name | Used by |
|-------------------------|-----------------------------|-----------------------------|
| Front-left drive motor | `front_left_drive` | Drive, odometry |
| Front-right drive motor | `front_right_drive` | Drive, odometry |
| Back-left drive motor | `back_left_drive` | Drive |
| Back-right drive motor | `back_right_drive` | Drive, odometry |
| HuskyLens | `HuskyLens` | Turret / target recognition |
The drive programs configure the left drive motors as `REVERSE` and the right drive motors as
`FORWARD`.
The odometry implementation uses three drive-motor encoders as tracking-wheel inputs. This
requires the selected encoders and sign conventions to match the actual robot setup.
Verify every hardware configuration name in the Robot Controller configuration before running
an OpMode.
## Development Status
| System                         | Current status                                                              |
|--------------------------------|-----------------------------------------------------------------------------|
| Basic mecanum TeleOp           | Implemented                                                                 |
| Field-centric mecanum TeleOp   | Implemented; requires physical validation                                   |
| Three-wheel odometry           | Implemented; calibration and testing required                               |
| PID movement controller        | Implemented; tuning and testing required                                    |
| Target-point movement helper   | Implemented as a helper class; not connected to an active autonomous OpMode |                                                           |
| HuskyLens tag recognition      | Initialized; detection and classification logic present                     |
| Automatic turret rotation      | Not implemented                                                             |
| Shooter sequence               | Not implemented                                                             |
| Intake subsystem               | Placeholder implementation                                                  |
| Competition autonomous routine | Not implemented                                                             |
## Building and Running
1. Clone or download the repository.
2. Open the project in a compatible version of Android Studio.
3. Allow Gradle to sync and resolve the FTC SDK dependencies.
4. Connect the Robot Controller hardware and verify the configured device names.
5. Build and install the Robot Controller app using the normal FTC development workflow.
6. Select an enabled OpMode from the Driver Station.
   Only run programs on the physical robot after verifying motor directions, power behavior, and
   sensor configuration. Test new movement and odometry code at low power in a controlled area.
## FTC Resources
- [FIRST Tech Challenge Documentation](https://ftc-docs.firstinspires.org/)
- [FTC Robot Controller SDK](https://github.com/FIRST-Tech-Challenge/FtcRobotController)
- [FTC SDK Javadoc](https://javadoc.io/doc/org.firstinspires.ftc)
- [FTC Community Forum](https://ftc-community.firstinspires.org/)
---
*This README documents the code in the repository snapshot used to write it. Update the
project structure and implementation status as robot code changes.*
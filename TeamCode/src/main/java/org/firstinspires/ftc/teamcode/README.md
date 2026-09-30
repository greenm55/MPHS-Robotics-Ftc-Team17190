# TeamCode Module

Welcome!

This module, `TeamCode`, contains the code used to control the team's FTC robot. It is organized into separate classes so that driving, turret control, odometry, and autonomous sequencing are handled independently.

## OpModes

### BasicMecanum

```text
Type: TeleOp
Drive: Mecanum
Control: Robot-centric
```

`BasicMecanum` is a manual TeleOp program that allows the driver to control the robot using mecanum drive.

### BasicTank

```text
Type: TeleOp
Drive: Tank
Control: Manual
```

`BasicTank` is a manual TeleOp program that allows the driver to control the robot using tank drive.

### AutoStateMachine

```text
Type: Autonomous
Drive: Mecanum
Navigation: Three-wheel odometry
Control: State machine
```

`AutoStateMachine` controls the overall autonomous sequence of the robot.

The state machine determines **what the robot should do**, while `RobotDrive` and `Turret` handle how those actions are performed.

The autonomous program uses target points in the following format:

```text
{X, Y, Heading, XY Tolerance, Heading Tolerance, Shoot}
```

Where:

| Value               | Description                     |
|---------------------|---------------------------------|
| `X`                 | Target X position               |
| `Y`                 | Target Y position               |
| `Heading`           | Target heading in radians       |
| `XY Tolerance`      | Maximum allowed position error  |
| `Heading Tolerance` | Maximum allowed heading error   |
| `Shoot`             | `0` = do not shoot, `1` = shoot |

Example:

```java
public static final double[][] targetPoints = {
        {100, 100, Math.PI, 50, 5, 0},
        {-100, 100, Math.PI / 2, 10, 1, 1},
        {0, 0, 0, 1, 1, 0}
};
```

## Autonomous Architecture

The autonomous system is divided into three main classes:

```text
AutoStateMachine
       |
       +---- RobotDrive
       |       |
       |       +---- Odometry
       |
       +---- Turret
```

### AutoStateMachine

`AutoStateMachine` is the main autonomous OpMode.

Its responsibility is to determine **what the robot should do next**.

It controls:

- Autonomous states
- Target point progression
- When the robot should move
- When the robot should shoot
- When the autonomous program is finished

`AutoStateMachine` does not directly control drive motors, odometry, or turret motors.

### RobotDrive

`RobotDrive` handles everything related to robot movement.

Its responsibilities include:

- Four mecanum drive motors
- Mecanum drive calculations
- Three-wheel odometry
- Robot position tracking
- Target-point movement
- Position and heading tolerances
- Determining whether the robot has reached a target
- Stopping the robot

`RobotDrive` uses `Odometry` to calculate the robot's position.

```text
RobotDrive
    |
    +---- Drive Motors
    |
    +---- Targeter
    |
    +---- At Point
    |
    +---- Odometry
```

### Turret

`Turret` handles everything related to the turret and shooting system.

Its responsibilities include:

- Turret movement
- Auto-aiming
- Shooter control
- Determining when a shooting sequence is complete

The state machine tells the turret **when** to shoot, while `Turret` handles **how** the turret shoots.

## Autonomous States

The autonomous state machine uses states to control the sequence of actions.

```text
MOVING
   |
   v
At Target?
   |
   +---- No ----> Keep Moving
   |
   +---- Yes
          |
          v
       Shoot?
       /     \
     No       Yes
     |         |
     v         v
 Next       SHOOTING
 Target        |
               v
          Shooting Done?
             |
             v
          Next Target
```

The main states are:

| State      | Description                                 |
|------------|---------------------------------------------|
| `MOVING`   | Robot moves toward the current target point |
| `SHOOTING` | Robot stops and the turret aims and shoots  |
| `FINISHED` | All target points have been completed       |

## Odometry

The autonomous program uses three-wheel odometry to determine the robot's position.

The odometry system provides:

```text
X
Y
Heading
```

The robot uses three encoder measurements:

```text
Front Right Encoder
Front Left Encoder
Back Encoder
```

The back encoder is the perpendicular odometry encoder.

`Odometry.java` performs the mathematical calculations required to convert encoder movement into the robot's position.

## Drive Motors

The four mecanum drive motors are:

| Motor       | Hardware Name       | Direction |
|-------------|---------------------|-----------|
| Front Left  | `front_left_drive`  | Reverse   |
| Front Right | `front_right_drive` | Forward   |
| Back Left   | `back_left_drive`   | Reverse   |
| Back Right  | `back_right_drive`  | Forward   |

The wheel-power array used by the drive calculations follows this order:

```text
0: Front Right
1: Front Left
2: Back Right
3: Back Left
```

## Project Structure

The main TeamCode classes are organized as follows:

```text
TeamCode
└── org.firstinspires.ftc.teamcode
    │
    ├── AutoStateMachine.java
    ├── RobotDrive.java
    ├── Turret.java
    ├── Odometry.java
    │
    ├── BasicMecanum.java
    └── BasicTank.java
```

This separation keeps each class focused on one responsibility:

```text
AutoStateMachine → What should happen?
RobotDrive       → How does the robot move?
Turret           → How does the turret aim and shoot?
Odometry         → Where is the robot?
```
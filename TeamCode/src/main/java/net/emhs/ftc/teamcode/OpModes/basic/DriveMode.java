package net.emhs.ftc.teamcode.OpModes.basic;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;

@TeleOp(name = "Drive Mode", group = "default")
public class DriveMode extends LinearOpMode {

    private DcMotor frontLeft, frontRight, backLeft, backRight, armLift, brush, shoulder;
    private Servo claw, elbowL, elbowR;

    double rightX1, rightY1, leftX1, leftY1, rightX2, rightY2, leftX2, leftY2, rightTrigger1, leftTrigger1, rightTrigger2, leftTrigger2;
    public double speed = 1;
    public int armSpeed = 5;


    // Change these values as needed
    // All are from zero to one
    final double defaultSpeed = 0.25; // Speed when no analog triggers are pressed.
    final double minSpeed = 0.05; // The minimum speed when slowing down using analog triggers. (Left trigger)
    final double maxSpeed = 0.80; // The maximum speed when speeding up using analog triggers. (Right trigger)

    final double armRaiseSpeed = 0.8; // The speed at which the arm is raised
    final double armLowerSpeed = 0.5;

    @Override
    public void runOpMode() {
        setUpDcMotors();
        setUpServos();

        waitForStart();

        while(opModeIsActive()) {
            updateVariables();
            updateMovement();

            telemetry.addData("Arm Position: ", armLift.getCurrentPosition());
            telemetry.update();
        }
    }

    public void updateMovement() {
        double denominator = Math.max(Math.abs(leftY1) + Math.abs(leftX1), 1);

        frontLeft.setPower(-((-leftY1 + leftX1 - rightX1) / denominator)*speed);
        backLeft.setPower(-((leftY1 + leftX1 - rightX1) / denominator)*speed);
        frontRight.setPower(((leftY1 + leftX1 + rightX1) / denominator)*speed);
        backRight.setPower(((-leftY1 + leftX1 + rightX1) / denominator)*speed);

        // All arm motion should be in this if statement to prevent conflicts
        if (rightY2 != 0) { // Manual control takes priority (Controller 2, Right stick)
            // TODO: make it good
            armLift.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            armLift.setPower(-rightY2);
        } else if (gamepad2.a) {
            // A button preset
            // runArmToPos(26935);
        } else if (gamepad2.b) {
            // B button preset
        } else if (gamepad2.x) {
            // X button preset
        } else if (gamepad2.y) {
            // Y button preset
        } else {
            armLift.setPower(0);
        }

        // All claw motion here
        claw.setPosition((gamepad2.right_trigger != 0) ? 1 : 0);

        // Shoulder motion
        if (leftY2 < 0) {
            shoulder.setPower(-leftY2);
        } else if (leftY2 > 0) { // Downward motion is at 10% of total possible speed
            shoulder.setPower(-leftY2 * 10/100);
        }
        else {
            shoulder.setPower(0);
        }

        // Elbow motion
        if (leftX2 != 0) {
            moveServo(elbowL, leftX2);
            moveServo(elbowR, -leftX2);
        }

        // Brush motion
        if (gamepad2.right_bumper) {
            brush.setPower(-1);
        } else if (gamepad2.left_bumper) {
            brush.setPower(1);
        } else {
            brush.setPower(0);
        }
    }

    private void updateVariables() {
        leftY1 = gamepad1.left_stick_y;
        leftX1 = gamepad1.left_stick_x;
        rightY1 = gamepad1.right_stick_y;
        rightX1 = gamepad1.right_stick_x;
        leftY2 = gamepad2.left_stick_y;
        leftX2 = gamepad2.left_stick_x;
        rightY2 = gamepad2.right_stick_y;
        rightX2 = gamepad2.right_stick_x;
        rightTrigger1 = gamepad1.right_trigger;
        leftTrigger1 = gamepad1.left_trigger;

        speed = defaultSpeed + (rightTrigger1 * (maxSpeed - defaultSpeed)) - (leftTrigger1 * (defaultSpeed - minSpeed));
        // TODO: Lower speed when arm is raised
        /*
        Takes the trigger value, normalizes it to the range between the min/max and the default
        so that it never goes over the max and under the min, adds the right trigger value and
        subtracts the left trigger.
        */

    }

    private void setUpDcMotors() {

        /*
        DC MOTORS:
        First four are the mecanum wheels
        Arm controls the height of the collapsible arm
         */

        frontRight = hardwareMap.get(DcMotor.class, "frontRight");
        frontLeft = hardwareMap.get(DcMotor.class, "frontLeft");
        backRight = hardwareMap.get(DcMotor.class, "backRight");
        backLeft = hardwareMap.get(DcMotor.class, "backLeft");
        armLift = hardwareMap.get(DcMotor.class, "armLift");
        brush = hardwareMap.get(DcMotor.class, "brush");
        shoulder = hardwareMap.get(DcMotor.class, "shoulder");

        DcMotor[] motors = { // Putting all DC Motors in an array allows for modifying each with a for loop
             frontRight, frontLeft, backRight, backLeft, armLift, brush, shoulder
        };

        for (DcMotor motor: motors) { // For each DC Motor in the array
            motor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            motor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        }

        armLift.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
    }

    private void setUpServos() {
        claw = hardwareMap.get(Servo.class, "claw");
        elbowL = hardwareMap.get(Servo.class, "elbowL");
        elbowR = hardwareMap.get(Servo.class, "elbowR");
        claw.setPosition(0.20);
    }

    private void runArmToPos (int pos) {
        armLift.setTargetPosition(pos);
        armLift.setMode(DcMotor.RunMode.RUN_TO_POSITION);
    }

    private void moveServo (Servo servo, double rate) {
        double pos = servo.getPosition();

        servo.setPosition(Math.max(Math.min(pos + rate/200, 1), 0));
    }
}

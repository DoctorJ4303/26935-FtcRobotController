package net.emhs.ftc.teamcode.OpModes.basic;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;

@TeleOp(name = "Drive Mode", group = "default")
public class DriveMode extends LinearOpMode {

    private DcMotor frontLeft, frontRight, backLeft, backRight, arm;
    private Servo claw;

    double rightX1, rightY1, leftX1, leftY1, rightX2, rightY2, leftX2, leftY2, rightTrigger1, leftTrigger1, rightTrigger2, leftTrigger2;
    public double speed = 1;

    // Change these values as needed
    // All are from zero to one
    final double defaultSpeed = 0.75; // Speed when no analog triggers are pressed.
    final double minSpeed = 0.20; // The minimum speed when slowing down using analog triggers. (Left trigger)
    final double maxSpeed = 1.00; // The maximum speed when speeding up using analog triggers. (Right trigger)

    final double armRaiseSpeed = 0.8; // The speed at which the arm is raised
    final double armLowerSpeed = 0.5;

    @Override
    public void runOpMode() throws InterruptedException {
        setUpMotors();

        waitForStart();

        while(opModeIsActive()) {
            updateVariables();
            updateMovement();
        }
    }

    public void updateMovement() {
        double denominator = Math.max(Math.abs(leftY1) + Math.abs(leftX1), 1);

        frontLeft.setPower(-((leftY1 - leftX1 + rightX1) / denominator)*speed);
        backLeft.setPower(-((leftY1 + leftX1 + rightX1) / denominator)*speed);
        frontRight.setPower(((leftY1 + leftX1 - rightX1) / denominator)*speed);
        backRight.setPower(((leftY1 - leftX1 - rightX1) / denominator)*speed);

        // All arm motion should be in this if statement to prevent conflicts
        if (rightY2 != 0) { // Manual control takes priority (Controller 2, Right stick)
            arm.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            arm.setPower(rightY2); // Manual arm control with right stick Y
        } else if (gamepad2.a) {
            // A button preset
            // runArmToPos(26935);
        } else if (gamepad2.b) {
            // B button preset
        } else if (gamepad2.x) {
            // X button preset
        } else if (gamepad2.y) {
            // Y button preset
        }

        // All claw motion here
        if (gamepad2.right_trigger != 0) {
            double currentPos = claw.getPosition();
            claw.setPosition(currentPos - 0.01);
        } else if (gamepad2.left_trigger != 0) {
            double currentPos = claw.getPosition();
            claw.setPosition(currentPos + 0.01);
        }
    }

    private void updateVariables() {
        leftY1 = gamepad1.left_stick_y;
        leftX1 = gamepad1.left_stick_x;
        rightY1 = gamepad1.right_stick_y;
        rightX1 = gamepad1.right_stick_x;
        leftY2 = gamepad1.left_stick_y;
        leftX2 = gamepad1.left_stick_x;
        rightY2 = gamepad1.right_stick_y;
        rightX2 = gamepad1.right_stick_x;
        rightTrigger1 = gamepad1.right_trigger;
        leftTrigger1 = gamepad1.left_trigger;

        speed = defaultSpeed + (rightTrigger1 * (maxSpeed - defaultSpeed)) - (leftTrigger1 * (defaultSpeed - minSpeed));
        /*
        Takes the trigger value, normalizes it to the range between the min/max and the default
        so that it never goes over the max and under the min, adds the right trigger value and
        subtracts the left trigger.
        */

    }

    private void setUpMotors() {

        /*
        DC MOTORS:
        First four are the mecanum wheels
        Arm controls the height of the collapsible arm
         */

        frontRight = hardwareMap.get(DcMotor.class, "frontRight");
        frontLeft = hardwareMap.get(DcMotor.class, "frontLeft");
        backRight = hardwareMap.get(DcMotor.class, "backRight");
        backLeft = hardwareMap.get(DcMotor.class, "backLeft");
        arm = hardwareMap.get(DcMotor.class, "arm");

        DcMotor[] motors = { // Putting all DC Motors in an array allows for modifying each with a for loop
             frontRight, frontLeft, backRight, backLeft, arm
        };

        for (DcMotor motor: motors) { // For each DC Motor in the array
            motor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            motor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        }


    }

    private void runArmToPos (int pos) {
        arm.setTargetPosition(pos);
        arm.setMode(DcMotor.RunMode.RUN_TO_POSITION);
    }
}

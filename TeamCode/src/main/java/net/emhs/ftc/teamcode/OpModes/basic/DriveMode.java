package net.emhs.ftc.teamcode.OpModes.basic;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

@TeleOp(name = "Drive Mode", group = "default")
public class DriveMode extends LinearOpMode {

    private DcMotor frontLeft, frontRight, backLeft, backRight;
    double rightX1, rightY1, leftX1, leftY1, rightX2, rightY2, leftX2, leftY2, rightTrigger1, leftTrigger1, rightTrigger2, leftTrigger2;
    public double speed = 1;

    // Change these values as needed
    // All are from zero to one
    double defaultSpeed = 0.75; // Speed when no analog triggers are pressed.
    double minSpeed = 0.20; // The minimum speed when slowing down using analog triggers. (Left trigger)
    double maxSpeed = 1.00; // The maximum speed when speeding up using analog triggers. (Right trigger)

    @Override
    public void runOpMode() throws InterruptedException {
        frontRight = hardwareMap.get(DcMotor.class, "frontLeft");
        frontLeft = hardwareMap.get(DcMotor.class, "frontRight");
        backRight = hardwareMap.get(DcMotor.class, "backLeft");
        backLeft = hardwareMap.get(DcMotor.class, "backRight");




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
}

package net.emhs.ftc.teamcode.OpModes.basic;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

@TeleOp(name = "Drive Mode", group = "default")
public class DriveMode extends LinearOpMode {

    private DcMotor frontLeft, frontRight, backLeft, backRight;
    private double rightX1, rightY1, leftX1, leftY1, rightX2, rightY2, leftX2, leftY2, rightTrigger1, leftTrigger1, rightTrigger2, leftTrigger2;
    public double speed = 1;


    @Override
    public void runOpMode() throws InterruptedException {
        frontRight = hardwareMap.get(DcMotor.class, "frontLeft");
        frontLeft = hardwareMap.get(DcMotor.class, "frontRight");
        backRight = hardwareMap.get(DcMotor.class, "backLeft");
        backLeft = hardwareMap.get(DcMotor.class, "backRight");




        while(opModeIsActive()) {
            leftY1 = gamepad1.left_stick_y;
            leftX1 = gamepad1.left_stick_x;
            rightY1 = gamepad1.right_stick_y;
            rightX1 = gamepad1.right_stick_x;
            leftY2 = gamepad1.left_stick_y;
            leftX2 = gamepad1.left_stick_x;
            rightY2 = gamepad1.right_stick_y;
            rightX2 = gamepad1.right_stick_x;

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
}

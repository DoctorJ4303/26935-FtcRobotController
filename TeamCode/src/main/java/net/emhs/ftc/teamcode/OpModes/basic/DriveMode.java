package net.emhs.ftc.teamcode.OpModes.basic;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.TouchSensor;

@TeleOp(name = "Drive Mode", group = "default")
public class DriveMode extends LinearOpMode {

    private DcMotor frontLeft, frontRight, backLeft, backRight, armLift1, armLift2, slider;
    private Servo claw, wrist, elbow1, elbow2, tilt;
    private CRServo brush;
    private TouchSensor endStop;

    double rightX1, rightY1, leftX1, leftY1, rightX2, rightY2, leftX2, leftY2, rightTrigger1, leftTrigger1, rightTrigger2, leftTrigger2;
    public double speed = 1;
    public int armSpeed = 5;


    // Change these values as needed
    // All are from zero to one
    final double defaultSpeed = 0.75; // Speed when no analog triggers are pressed.
    final double minSpeed = 0.20; // The minimum speed when slowing down using analog triggers. (Left trigger)
    final double maxSpeed = 1.00; // The maximum speed when speeding up using analog triggers. (Right trigger)

    final double armRaiseSpeed = 0.8; // The speed at which the arm is raised
    final double armLowerSpeed = 0.5;

    @Override
    public void runOpMode() {
        endStop = hardwareMap.get(TouchSensor.class, "endStop");
        setUpDcMotors();
        setUpServos();

        waitForStart();

        while(opModeIsActive()) {
            telemetry.addData("Slider Pos: ", slider.getCurrentPosition());
            telemetry.update();

            updateVariables();
            updateMovement();
        }
    }

    public void updateMovement() {
        double denominator = Math.max(Math.abs(leftY1) + Math.abs(leftX1), 1);

        frontLeft.setPower(-((-leftY1 + leftX1 - rightX1) / denominator)*speed);
        backLeft.setPower(-((leftY1 + leftX1 - rightX1) / denominator)*speed);
        frontRight.setPower(((leftY1 + leftX1 + rightX1) / denominator)*speed);
        backRight.setPower(((-leftY1 + leftX1 + rightX1) / denominator)*speed);

        // All arm motion should be in this if statement to prevent conflicts
        if (leftY2 != 0) { // Manual control takes priority (Controller 2, Right stick)
            armLift1.setPower(-leftY2);
            armLift2.setPower(leftY2);
        } else {
            armLift1.setPower(0);
            armLift2.setPower(0);
        }
        if (gamepad2.a) {
            // A button preset
            // runArmToPos(26935);
        } else if (gamepad2.b) {
            // B button preset
        } else if (gamepad2.x) {
            // X button preset
        } else if (gamepad2.y) {
            // Y button preset
        }/* else {
            armLift.setTargetPosition(armLift.getCurrentPosition()); // Keeps the lift in place
            armLift.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            armLift2.setTargetPosition(armLift2.getCurrentPosition());
            armLift2.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        }*/

        // All claw motion here
        if (gamepad2.left_bumper) {
           claw.setPosition(1);
        } else {
            claw.setPosition(0);
        }

        if (gamepad2.right_trigger != 0){
            moveServo(wrist, 2);
        } else if (gamepad2.left_trigger != 0){
            moveServo(wrist, -2);
        }
        // Elbow swinging movement
        if (gamepad2.left_stick_x > 0){
            moveServo(elbow1, 1);
            moveServo(elbow2, -1);
        } else if (gamepad2.left_stick_x < 0){
            moveServo(elbow1, -1);
            moveServo(elbow2, 1);
        }

        // Slider movement
        if (endStop.isPressed()) { // Button is pressed
            slider.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            slider.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            if (gamepad2.right_stick_y > 0.1) { // Button is pressed and joystick is POSITIVE
                slider.setTargetPosition(-(int)(gamepad2.right_stick_y*500));
                slider.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                slider.setPower(1);
            }
        } else if (Math.abs(gamepad2.right_stick_y) > 0.1) { // Joystick is active and button is not pressed
            slider.setTargetPosition(-(int)(gamepad2.right_stick_y*500)); // Move according to joystick
            slider.setMode(DcMotor.RunMode.RUN_TO_POSITION); // Go to previously set position
            slider.setPower(1);
        } else { // Joystick is inactive and button is not pressed
            slider.setPower(0); // No movement
        }

        //tilt movement
        if (gamepad2.right_stick_x > 0){
            moveServo(tilt, 2);
        }else if(gamepad2.right_stick_x < 0){
            moveServo(tilt, -2);
        }

        // Brush motion
        if (gamepad2.right_bumper) {
            brush.setPower(1);
        }else{
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
        armLift1 = hardwareMap.get(DcMotor.class, "armLift1");
        armLift2 = hardwareMap.get(DcMotor.class, "armLift2");
        slider = hardwareMap.get(DcMotor.class, "slider");

        DcMotor[] motors = { // Putting all DC Motors in an array allows for modifying each with a for loop
             frontRight, frontLeft, backRight, backLeft, armLift1, armLift2, slider
        };

        DcMotor[] brakeMotors = { // These motors use brake zero power behavior to resist external forces
                armLift1, armLift2 // Resist gravity
        };

        for (DcMotor motor: motors) { // For each DC Motor in the array
            motor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            motor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        }

        while (!endStop.isPressed()) {
            slider.setPower(-.2);
        }
        slider.setPower(0);
        slider.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
    }

    private void setUpServos() {
        claw = hardwareMap.get(Servo.class, "claw");
        wrist = hardwareMap.get(Servo.class, "wrist");
        elbow1 = hardwareMap.get(Servo.class, "elbow1");
        elbow2 = hardwareMap.get(Servo.class, "elbow2");
        tilt = hardwareMap.get(Servo.class, "tilt");
        brush = hardwareMap.get(CRServo.class, "brush");

        brush.setDirection(CRServo.Direction.REVERSE);
    }

    private void moveServo (Servo servo, double rate) {
        double pos = servo.getPosition();

        servo.setPosition(Math.max(Math.min(pos + rate/200, 1), 0));
    }
}

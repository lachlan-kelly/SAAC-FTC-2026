package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo; // 1. Add this import

@TeleOp(name = "mecanumSimplified")
public class mecnumFullTest extends LinearOpMode {

    private DcMotor frontLeft;
    private DcMotor backLeft;
    private DcMotor frontRight;
    private DcMotor backRight;
    
    // 2. Change the type from DcMotor to Servo
    private Servo armServo; 

    @Override
    public void runOpMode() {
        frontLeft = hardwareMap.get(DcMotor.class, "frontLeft");
        backLeft = hardwareMap.get(DcMotor.class, "backLeft");
        frontRight = hardwareMap.get(DcMotor.class, "frontRight");
        backRight = hardwareMap.get(DcMotor.class, "backRight");
        
        // 3. Change DcMotor.class to Servo.class
        armServo = hardwareMap.get(Servo.class, "armServo");

        // Set directions for drive motors (Servos do not need this)
        frontLeft.setDirection(DcMotor.Direction.FORWARD);
        backLeft.setDirection(DcMotor.Direction.FORWARD);
        frontRight.setDirection(DcMotor.Direction.FORWARD);
        backRight.setDirection(DcMotor.Direction.FORWARD);

        // Set run modes for drive motors (Servos do not need this)
        frontLeft.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        backLeft.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        frontRight.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        backRight.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        // 4. Set an initial position for the servo before the match starts (0.0 to 1.0)
        armServo.setPosition(0.5); 

        waitForStart();

        while (opModeIsActive()) {
            // Forwards and back on the left stick Y-axis.
            double forward = -gamepad1.left_stick_y;

            // Strafing on the left stick X-axis.
            double strafe = gamepad1.left_stick_x;

            // Turning on the right stick X-axis.
            double turn = gamepad1.right_stick_x;

            double frontLeftPower = forward + strafe - turn;
            double backLeftPower = forward - strafe - turn;
            double frontRightPower = forward - strafe + turn;
            double backRightPower = forward + strafe + turn;

            double maxPower = Math.max(1.0, Math.max(
                    Math.max(Math.abs(frontLeftPower), Math.abs(backLeftPower)),
                    Math.max(Math.abs(frontRightPower), Math.abs(backRightPower))
            ));

            frontLeft.setPower(frontLeftPower / maxPower);
            backLeft.setPower(backLeftPower / maxPower);
            frontRight.setPower(frontRightPower / maxPower);
            backRight.setPower(backRightPower / maxPower);

            // 5. Add Servo Control Logic
            // Example: Use D-Pad Up and Down to move the arm
            if (gamepad1.dpad_up) {
                armServo.setPosition(1.0); // Move to max position
            } else if (gamepad1.dpad_down) {
                armServo.setPosition(0.5); // Move to min position
            }
            
            telemetry.addData("FL", frontLeftPower / maxPower);
            telemetry.addData("BL", backLeftPower / maxPower);
            telemetry.addData("FR", frontRightPower / maxPower);
            telemetry.addData("BR", backRightPower / maxPower);
            telemetry.addData("Arm Servo Position", armServo.getPosition()); // Show servo pos on Driver Hub
            telemetry.update();
        }
    }
}
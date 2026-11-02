package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

@TeleOp(name = "mecanumSimplified")
public class mecnumFullTest extends LinearOpMode {

    private DcMotor frontLeft;
    private DcMotor backLeft;
    private DcMotor frontRight;
    private DcMotor backRight;
    private DcMotor armServo;

    @Override
    public void runOpMode() {
        frontLeft = hardwareMap.get(DcMotor.class, "frontLeft");
        backLeft = hardwareMap.get(DcMotor.class, "backLeft");
        frontRight = hardwareMap.get(DcMotor.class, "frontRight");
        backRight = hardwareMap.get(DcMotor.class, "backRight");
        armServo = hardwareMap.get(DcMotor.class, "armServo");

        // Adjust the motors' directions based on your drivetrain.
        // These match the original Blocks program (all FORWARD).
        frontLeft.setDirection(DcMotor.Direction.FORWARD);
        backLeft.setDirection(DcMotor.Direction.FORWARD);
        frontRight.setDirection(DcMotor.Direction.FORWARD);
        backRight.setDirection(DcMotor.Direction.FORWARD);

        frontLeft.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        backLeft.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        frontRight.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        backRight.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        waitForStart();

        while (opModeIsActive()) {
            // Forwards and back on the left stick Y-axis.
            double forward = -gamepad1.left_stick_y;

            // Strafing on the left stick X-axis.
            double strafe = gamepad1.left_stick_x;

            // Turning on the right stick X-axis.
            // Sign matches the original Blocks program: left side gets -x, right side gets +x.
            double turn = gamepad1.right_stick_x;

            // The Blocks program wrote each motor's power three times per loop, so only the
            // last write (turning) took effect. Here the three inputs are summed instead.
            double frontLeftPower = forward + strafe - turn;
            double backLeftPower = forward - strafe - turn;
            double frontRightPower = forward - strafe + turn;
            double backRightPower = forward + strafe + turn;

            // Scale all powers together if any value exceeds 1, which preserves direction.
            double maxPower = Math.max(1.0, Math.max(
                    Math.max(Math.abs(frontLeftPower), Math.abs(backLeftPower)),
                    Math.max(Math.abs(frontRightPower), Math.abs(backRightPower))
            ));

            frontLeft.setPower(frontLeftPower / maxPower);
            backLeft.setPower(backLeftPower / maxPower);
            frontRight.setPower(frontRightPower / maxPower);
            backRight.setPower(backRightPower / maxPower);

            telemetry.addData("FL", frontLeftPower / maxPower);
            telemetry.addData("BL", backLeftPower / maxPower);
            telemetry.addData("FR", frontRightPower / maxPower);
            telemetry.addData("BR", backRightPower / maxPower);
            telemetry.update();
        }
    }
}
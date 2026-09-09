package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

@TeleOp(name = "mecanumFull (Blocks to Java)")
public class mecanumFull extends LinearOpMode {

    private DcMotor frontLeft;
    private DcMotor frontRight;
    private DcMotor backLeft;
    private DcMotor backRight;

    /**
     * This function is executed when this Op Mode is selected.
     */
    @Override
    public void runOpMode() {
        frontLeft = hardwareMap.get(DcMotor.class, "frontLeft");
        frontRight = hardwareMap.get(DcMotor.class, "frontRight");
        backLeft = hardwareMap.get(DcMotor.class, "backLeft");
        backRight = hardwareMap.get(DcMotor.class, "backRight");

        // Put initialization blocks here.
        MOTOR_SETTINGS();
        waitForStart();
        if (opModeIsActive()) {
            // Put run blocks here.
            while (opModeIsActive()) {
                MECANUM_DRIVE();
            }
        }
    }

    /**
     * Describe this function...
     */
    private void MOTOR_SETTINGS() {
        frontLeft.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        frontLeft.setDirection(DcMotor.Direction.FORWARD);
        frontRight.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        frontRight.setDirection(DcMotor.Direction.FORWARD);
        backLeft.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        backLeft.setDirection(DcMotor.Direction.FORWARD);
        backRight.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        backRight.setDirection(DcMotor.Direction.FORWARD);
    }

    /**
     * Describe this function...
     */
    private void MECANUM_DRIVE() {
        float forwardBack;
        float strafe;
        float turn;
        float leftFrontPower;
        float rightFrontPower;
        float leftBackPower;
        float rightBackPower;

        // Determining movement based on gamepad inputs
        forwardBack = -gamepad1.left_stick_y;
        strafe = gamepad1.left_stick_x;
        turn = gamepad1.right_stick_x;
        leftFrontPower = forwardBack + strafe + turn;
        rightFrontPower = (forwardBack - strafe) - turn;
        leftBackPower = (forwardBack - strafe) + turn;
        rightBackPower = (forwardBack + strafe) - turn;
        // Setting Motor Power
        frontLeft.setPower(leftFrontPower);
        frontRight.setPower(rightFrontPower);
        backLeft.setPower(leftBackPower);
        backRight.setPower(rightBackPower);
    }
}
package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

@TeleOp(name = "ControllerTest (Blocks to Java)")
public class ControllerTest extends LinearOpMode {

    private DcMotor right_drive_3;
    private DcMotor left_drive_2;

    /**
     * This sample contains the bare minimum Blocks for any regular OpMode. The 3 blue
     * Comment Blocks show where to plae Initialization code (runs once, after touching the
     * DS INIT button, and before touching the DS Start arrow), Run code (runs once, after
     * touching Start), and Loop code (runs repeatedly while the OpMode is active, namely not
     * Stopped).
     */
    @Override
    public void runOpMode() {
        float drive_pwr;
        float turn_pwr;
        double tgtPower;

        right_drive_3 = hardwareMap.get(DcMotor.class, "right_drive_3");
        left_drive_2 = hardwareMap.get(DcMotor.class, "left_drive_2");

        right_drive_3.setDirection(DcMotor.Direction.REVERSE);
        left_drive_2.setDirection(DcMotor.Direction.FORWARD);
        waitForStart();
        if (opModeIsActive()) {
            while (opModeIsActive()) {
                drive_pwr = -gamepad1.left_stick_y;
                turn_pwr = -gamepad1.left_stick_x;
                if (turn_pwr > 0) {
                    right_drive_3.setPower(drive_pwr - turn_pwr);
                    left_drive_2.setPower(drive_pwr);
                } else {
                    left_drive_2.setPower(drive_pwr - turn_pwr);
                    right_drive_3.setPower(drive_pwr);
                }
                telemetry.addData("Target Power", tgtPower);
                telemetry.update();
            }
        }
    }
}

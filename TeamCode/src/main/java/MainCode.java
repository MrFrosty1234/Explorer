import com.acmerobotics.dashboard.FtcDashboard;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.TouchSensor;

import org.firstinspires.ftc.robotcore.external.State;


@TeleOp

public class MainCode extends LinearOpMode {

    Explorer explorer = null;
    public static double sepraratorMove;

    TouchSensor unbbubl;


    public void runOpMode() {

        unbbubl = hardwareMap.get(TouchSensor.class, "startButton");

        explorer = new Explorer(this);

        int color = explorer.colorDetective.fieldDetect();

        if (color == 1) {
            explorer.colorDetective.ourColor = 1;
            explorer.colorDetective.notOurColor = 2;
        }
        if (color == 2) {
            explorer.colorDetective.ourColor = 2;
            explorer.colorDetective.notOurColor = 1;
        }


        waitForStart();



        explorer.driveTrain.state = DriveTrain.State.MOVING_TO_WALL;

        while (!unbbubl.isPressed())
            sleep(1);
        explorer.driveTrain.ti.reset();

        while (opModeIsActive()) {

            explorer.sortingAndKeep.brushOn();
            explorer.colorDetective.sorting();
            explorer.colorDetective.getOut();

            explorer.update();


            // FtcDashboard.getInstance().getTelemetry().addData("time", explorer.driveTrain.ti.seconds());
            FtcDashboard.getInstance().getTelemetry().addData("button", explorer.driveTrain.button.getValue());
            FtcDashboard.getInstance().getTelemetry().addData("time", explorer.driveTrain.ti.seconds());
            FtcDashboard.getInstance().getTelemetry().addData("red", explorer.colorDetective.colorFieldSensor.red());
            FtcDashboard.getInstance().getTelemetry().addData("blue", explorer.colorDetective.colorFieldSensor.blue());
            FtcDashboard.getInstance().getTelemetry().addData("green", explorer.colorDetective.colorFieldSensor.green());
            FtcDashboard.getInstance().getTelemetry().addData("color", color);
            FtcDashboard.getInstance().getTelemetry().update();
        }
    }
}
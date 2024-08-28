import com.acmerobotics.dashboard.FtcDashboard;
import com.qualcomm.hardware.modernrobotics.ModernRoboticsTouchSensor;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.TouchSensor;

@Autonomous

public class MainCode extends LinearOpMode {

    SortingAndKeep sortingAndKeep;
    Explorer explorer = null;
    public static double sepraratorMove;

    TouchSensor startButton;


    public void runOpMode() {

        startButton = explorer.linearOpMode.hardwareMap.get(TouchSensor.class, "startButton");

        explorer = new Explorer(this);

        explorer.colorDetective.ourColor = 1;
        explorer.colorDetective.notOurColor = 2;

        waitForStart();

        // while(startButton.getValue() < 0.1)
        //   sleep(1);

        boolean go = true;
        explorer.driveTrain.ti.reset();

        while (opModeIsActive()) {
            explorer.sortingAndKeep.brushOn();


            FtcDashboard.getInstance().getTelemetry().addData("color", explorer.colorDetective.puckDetect());
            FtcDashboard.getInstance().getTelemetry().update();
        }
    }
}

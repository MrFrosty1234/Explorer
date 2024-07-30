public class DriveTrain {
    PID pid;

    public static double kPX = 0;
    public static double kDX = 0;
    public static double kIX = 0;
    public DriveTrain(){
        pid = new PID(kPX,kDX,kIX);
    }
}

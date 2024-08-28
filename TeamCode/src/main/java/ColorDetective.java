import static com.qualcomm.hardware.ams.AMSColorSensor.AMS_TCS34725_ADDRESS;

import static java.lang.Math.round;
import static java.lang.Math.sqrt;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.hardware.adafruit.AdafruitI2cColorSensor;
import com.qualcomm.hardware.ams.AMSColorSensor;
import com.qualcomm.robotcore.hardware.I2cDeviceSynchDeviceWithParameters;

import java.lang.reflect.Field;

@Config

public class ColorDetective {

    AdafruitI2cColorSensor colorFieldSensor;
    AdafruitI2cColorSensor colorPuckDetectiveSensor;

    Explorer explorer;

    public static long puckRedRed = 220;
    public static long puckRedGreen = 100;
    public static long puckRedBlue = 93;
    public static long puckBlueRed = 63;
    public static long puckBlueGreen = 98;
    public static long puckBlueBlue = 125;
    public static double minCosPuck = 0.95;
    public static long puckFieldRed = 1050;
    public static long puckFieldGreen = 1100;
    public static long puckFieldBlue = 1050;


    public static long fieldRedRed = 117;
    public static long fieldRedGreen = 60;
    public static long fieldRedBlue = 58;
    public static long fieldBlueRed = 38;
    public static long fieldBlueGreen = 64;
    public static long fieldBlueBlue = 83;
    public static double minCosField = 0.95;
    public static long fieldFieldRed = 230;
    public static long fieldFieldGreen = 281;
    public static long fieldFieldBlue = 203;


    public int ourColor = 0;

    public int notOurColor = 0;

    public ColorDetective(Explorer robot) {
        explorer = robot;
        colorFieldSensor = fix(explorer.linearOpMode.hardwareMap.get(AdafruitI2cColorSensor.class, "fieldSensor"));
        colorPuckDetectiveSensor = fix(explorer.linearOpMode.hardwareMap.get(AdafruitI2cColorSensor.class, "puckSensor"));
    }

    double search(AdafruitI2cColorSensor sensor, long r1, long g1, long b1) {
        double len1, len2, len3;
        double cosA;
        len1 = sqrt(r1 * r1 + g1 * g1 + b1 * b1);
        len2 = sqrt(sensor.red() * sensor.red() + sensor.green() * sensor.green() + sensor.blue() * sensor.blue());
        len3 = sqrt((r1 - sensor.red()) * (r1 - sensor.red()) + (g1 - sensor.green()) * (g1 - sensor.green()) + (b1 - sensor.blue()) * (b1 - sensor.blue()));
        if (len1 * len2 != 0) {
            cosA = (len1 * len1 + len2 * len2 - len3 * len3) / (2 * len1 * len2);
        } else cosA = 0;
        return cosA;
    }

    //color = 0 if void, color = 1 if its blue, color = 2 if its red//

    int puckSearch() {
        double colorRed = search(colorPuckDetectiveSensor, puckRedRed, puckRedGreen, puckRedBlue);
        double colorBlue = search(colorPuckDetectiveSensor, puckBlueRed, puckBlueGreen, puckBlueBlue);
        double colorVoid = search(colorPuckDetectiveSensor, puckFieldRed, puckFieldGreen, puckFieldBlue);

        int color = -1;
        if (colorBlue < minCosPuck && colorRed < minCosPuck && colorVoid > minCosPuck)
            color = 0;
        if (colorBlue > colorRed && colorBlue > colorVoid)
            color = 1;
        if (colorRed > colorBlue && colorRed > colorVoid)
            color = 2;
        return color;
    }

    int puckDetect() {
        if (colorPuckDetectiveSensor.red() > 30 && colorPuckDetectiveSensor.blue() > 30 && colorPuckDetectiveSensor.green() > 30) {
            if (colorPuckDetectiveSensor.red() > colorPuckDetectiveSensor.blue())
                return 1;
            if (colorPuckDetectiveSensor.blue() > colorPuckDetectiveSensor.red())
                return 2;
        } else return 0;
        return 0;
    }

    int fieldDetect() {
        if (colorFieldSensor.red() < 200 && colorFieldSensor.blue() < 200 && colorFieldSensor.green() < 200 ) {
            if (colorFieldSensor.red() > colorFieldSensor.blue())
                return 1;
            if (colorFieldSensor.blue() > colorFieldSensor.red())
                return 2;
        } else return 0;
        return 0;
    }

    public boolean statePuck = false;
    public double time = System.currentTimeMillis() / 1000.0;


    public boolean sorting() {
        double t = System.currentTimeMillis() / 1000.0;
        int color = puckDetect();
        int field = fieldDetect();
        if (statePuck) {
            statePuck = explorer.sortingAndKeep.separatorPosition(0);
            return false;
        }
        if (t - time > 0.5 && field != 1 && field != 2) {
            t = System.currentTimeMillis() / 1000.0;
            if (color == ourColor) {
                statePuck = explorer.sortingAndKeep.separatorPosition(1);
                time = t;
                return true;
            }
            if (color == notOurColor) {
                statePuck = explorer.sortingAndKeep.separatorPosition(-1);
                time = t;
                return true;
            }
        }
        statePuck = explorer.sortingAndKeep.separatorPosition(0);
        return false;

    }

    public boolean getOut() {
        int color = fieldDetect();
        if (color == 0 || color == notOurColor) {
            explorer.sortingAndKeep.closeServo();
            explorer.sortingAndKeep.separatorPosition(0);
            return false;
        }
        if (color == ourColor) {
            explorer.sortingAndKeep.separatorPosition(0);
            explorer.sortingAndKeep.openServo();
            return true;
        }
        return false;
    }


    public static AdafruitI2cColorSensor fix(AdafruitI2cColorSensor sensor) {
        try {
            AMSColorSensor.class.getDeclaredField("AMS_TCS34725_ID").setAccessible(true);

            AMSColorSensor.Parameters parameters = new AMSColorSensor.Parameters(AMS_TCS34725_ADDRESS, 0x4D);

            Field paramField = I2cDeviceSynchDeviceWithParameters.class.getDeclaredField("parameters");

            paramField.setAccessible(true);

            try {
                paramField.set(sensor, parameters);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            sensor.initialize();
        } catch (NoSuchFieldException e) {
            throw new RuntimeException("color sensor hack not successful");
        }

        return sensor;
    }

}

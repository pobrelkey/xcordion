package xcordion.test;

import xcordion.junit3.XcordionTestCase;

import java.util.Date;
import java.util.GregorianCalendar;
import java.text.SimpleDateFormat;

public class SimpleHappyPathXcordionTest extends XcordionTestCase {

    private Hell hell = new Hell();
    private static final String[] MONSTERS = new String[]{ "Inky", "Binky", "Pinky", "Clyde" };
    private static final Stooge[] STOOGES = new Stooge[]{
            new Stooge("Harry Moses Horwitz",   "Moe",   date(19,  6, 1897)),
            new Stooge("Louis Feinberg",        "Larry", date( 5, 10, 1902)),
            new Stooge("Jerome Lester Horwitz", "Curly", date(22, 10, 1903)),
            new Stooge("Samuel Horwitz",        "Shemp", date( 4,  3, 1895)),
    };

    private static Date date(int day, int month, int year) {
        GregorianCalendar c = new GregorianCalendar();
        c.set(year, month-1, day);
        return c.getTime();
    }


    public String getName() {
        return "Billy";
    }

    public Pope getPope() {
        return new Pope();
    }

    private class Pope {
    	public String getHatType() {
    		return "pointy";
    	}

        public PapalReligion getReligion() {
            return new PapalReligion();
        }

        private class PapalReligion {
            public boolean isCatholic() {
                return true;
            }
        }
    }

    private class Hell {
        private int temperatureCelsius = 99;

        public int getTemperatureCelsius() {
            return temperatureCelsius;
        }

        public void turnOnAirCon() {
            temperatureCelsius -= 100;
        }
    }

    public String[] getMonstersFromPacMan() {
        return MONSTERS;
    }

    public double hypotenuse(double sideA, double sideB) {
        return Math.sqrt(sideA * sideA + sideB * sideB);
    }

    public Stooge[] getStooges() {
        return STOOGES;
    }

    public Hell getHell() {
        return hell;
    }

    private static class Stooge {
        private String birthName, nickname;
        private Date dateOfBirth;

        static private final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("d MMMM yyyy");

        private Stooge(String birthName, String nickname, Date dateOfBirth) {
            this.birthName = birthName;
            this.nickname = nickname;
            this.dateOfBirth = dateOfBirth;
        }

        public String getBirthName() {
            return birthName;
        }

        public String getNickname() {
            return nickname;
        }

        public String getDateOfBirth() {
            return DATE_FORMAT.format(dateOfBirth);
        }
    }
}

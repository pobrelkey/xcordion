using System;
using System.Text;
using System.Collections.Generic;
using Microsoft.VisualStudio.TestTools.UnitTesting;
using xcordion.lang.csharp;

namespace test
{
    [TestClass]
    public class SimpleHappyPathXcordionTest
    {
        [TestMethod]
        public void ProcessDocument()
        {
            new SimpleXcordionRunner(this).runTest();
        }

    
        private Hell hell = new Hell();
        private readonly string[] MONSTERS = new string[]{ "Inky", "Binky", "Pinky", "Clyde" };
        private readonly Stooge[] STOOGES = new Stooge[]{
                new Stooge("Harry Moses Horwitz",   "Moe",   new DateTime(1897,  6, 19)),
                new Stooge("Louis Feinberg",        "Larry", new DateTime(1902, 10,  5)),
                new Stooge("Jerome Lester Horwitz", "Curly", new DateTime(1903, 10, 22)),
                new Stooge("Samuel Horwitz",        "Shemp", new DateTime(1895,  3,  4)),
        };

        public string Name {
            get { return "Billy"; }
        }

        public Pope Pope {
            get { return new Pope(); }
        }


        public String[] MonstersFromPacMan {
            get { return MONSTERS; }
        }

        public double hypotenuse(double sideA, double sideB) {
            return Math.Sqrt(sideA * sideA + sideB * sideB);
        }

        public Stooge[] Stooges {
            get { return STOOGES; }
        }

        public Hell Hell {
            get { return hell; }
        }

    }

    public class Stooge {
        private string birthName, nickname;
        private DateTime dateOfBirth;

        public Stooge(string birthName, string nickname, DateTime dateOfBirth) {
            this.birthName = birthName;
            this.nickname = nickname;
            this.dateOfBirth = dateOfBirth;
        }

        public string BirthName {
            get { return birthName; }
        }

        public string Nickname {
            get { return nickname; }
        }

        public string DateOfBirth {
            get { return dateOfBirth.ToString("d MMMM yyyy"); }
        }
    }

    public class Hell {
        private int temperatureCelsius = 99;

        public int TemperatureCelsius {
            get { return temperatureCelsius; }
        }

        public void turnOnAirCon() {
            temperatureCelsius -= 100;
        }
    }

    public class Pope {
    	public string HatType {
            get { return "pointy"; }
    	}

        public PapalReligion Religion {
            get { return new PapalReligion(); }
        }

    }

    public class PapalReligion {
        public bool Catholic {
            get { return true; }
        }
    }

}

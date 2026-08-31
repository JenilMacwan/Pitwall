package com.jenil.f1comp.util

import com.jenil.f1comp.data.model.DriverAboutInfo
import com.jenil.f1comp.data.model.TeamAboutInfo

object ProfileUtils {

    fun getFlagEmoji(nationality: String?): String {
        return when (nationality?.lowercase()) {
            "british" -> "🇬🇧"
            "dutch" -> "🇳🇱"
            "monégasque", "monegasque" -> "🇲🇨"
            "spanish" -> "🇪🇸"
            "mexican" -> "🇲🇽"
            "australian" -> "🇦🇺"
            "german" -> "🇩🇪"
            "french" -> "🇫🇷"
            "canadian" -> "🇨🇦"
            "japanese" -> "🇯🇵"
            "thai" -> "🇹🇭"
            "danish" -> "🇩🇰"
            "chinese" -> "🇨🇳"
            "finnish" -> "🇫🇮"
            "brazilian" -> "🇧🇷"
            "italian" -> "🇮🇹"
            "american" -> "🇺🇸"
            "austrian" -> "🇦🇹"
            "swiss" -> "🇨🇭"
            "new zealander" -> "🇳🇿"
            "argentine" -> "🇦🇷"
            "belgian" -> "🇧🇪"
            else -> "🏁"
        }
    }

    fun getChassis(constructorId: String?): String {
        return when (constructorId?.lowercase()) {
            "mercedes" -> "F1 W17"
            "red_bull", "redbull" -> "RB22"
            "ferrari" -> "SF-26"
            "mclaren" -> "MCL40"
            "aston_martin" -> "AMR26"
            "alpine" -> "A526"
            "williams" -> "FW48"
            "rb", "racing_bulls" -> "VCARB 03"
            "audi", "Audi" -> "R26"
            "haas" -> "VF-26"
            "cadillac" -> "MAC26"
            else -> "TBD"
        }
    }

    fun getPowerUnit(constructorId: String?): String {
        return when (constructorId?.lowercase()) {
            "mercedes", "williams", "mclaren", "aston_martin", "alpine" -> "Mercedes-AMG"
            "red_bull", "redbull", "rb", "racing_bulls" -> "Red Bull Ford"
            "ferrari", "haas", "cadillac" -> "Ferrari"
            "audi", "Audi" -> "Audi"
            else -> "TBD"
        }
    }

    fun getTeamPrincipal(constructorId: String?): String {
        return when (constructorId?.lowercase()) {
            "mercedes" -> "Toto Wolff"
            "red_bull", "redbull" -> "Laurent Mekies"
            "ferrari" -> "Frédéric Vasseur"
            "mclaren" -> "Andrea Stella"
            "aston_martin" -> "Adrain Newey"
            "alpine" -> "Flavio Briatore"
            "williams" -> "James Vowles"
            "rb", "racing_bulls" -> "Alan Permane"
            "audi" -> "Jonathan Wheatley"
            "haas" -> "Ayao Komatsu"
            "cadillac" -> "Graeme Lowdon"
            else -> "TBD"
        }
    }

    fun getTeamAbout(constructorId: String?): TeamAboutInfo {
        return when (constructorId?.lowercase()) {
            "mercedes" -> TeamAboutInfo(
                fullName = "Mercedes-AMG PETRONAS F1 Team",
                base = "Brackley, Northamptonshire, United Kingdom",
                about = "The modern juggernaut that rewrote the record books by winning eight consecutive Constructors' Championships (2014–2021). Operating out of motorsport valley in the UK with engines built in Brixworth, the Silver Arrows are defined by engineering rigor, operational execution, and a fresh generational driver pairing in George Russell and Andrea Kimi Antonelli."
            )
            "red_bull", "redbull" -> TeamAboutInfo(
                fullName = "Oracle Red Bull Racing",
                base = "Bradbourne Drive, Milton Keynes, Buckinghamshire, United Kingdom",
                about = "Emerging from an energy drinks giant in 2005, Red Bull revolutionized the sport with aggressive aero philosophy, sharp pit work, and ruthless track execution. The 2026 season marks their boldest era yet as they transition from customer engines to building their own in-house power unit in technical collaboration with Ford."
            )
            "ferrari" -> TeamAboutInfo(
                fullName = "Scuderia Ferrari HP",
                base = "Via Enzo Ferrari 27, Maranello, Emilia-Romagna, Italy",
                about = "The beating heart of Formula 1 and the only constructor to compete in every single World Championship season since 1950. Ferrari represents pure motorsport romance and relentless pressure; their scarlet cars carry 16 Constructors' titles, backed by the unmatched passion of the global Tifosi and a blockbuster lineup featuring Charles Leclerc and Lewis Hamilton."
            )

            "mclaren" -> TeamAboutInfo(
                fullName = "McLaren Mastercard F1 Team",
                base = "McLaren Technology Centre, Woking, Surrey, United Kingdom",
                about = "Founded in 1963 by Bruce McLaren, this iconic British team is the second-most successful constructor in F1 history. Known for their distinct papaya livery, McLaren has surged back to the sharp end of the grid as championship front-runners with a lethal mix of aerodynamic efficiency and top-tier driver talent in Lando Norris and Oscar Piastri."
            )
            "aston_martin" -> TeamAboutInfo(
                fullName = "Aston Martin Aramco F1 Team",
                base = "Dadford Road, Silverstone, Northamptonshire, United Kingdom",
                about = "The historic British luxury brand backed by billionaire Lawrence Stroll's massive investment, including a brand-new factory and wind tunnel campus at Silverstone. With design maestro Adrian Newey steering technical direction and a works Honda engine deal, Aston Martin is built to contend for world titles."
            )
            "alpine" -> TeamAboutInfo(
                fullName = "BWT Alpine F1 Team",
                base = "Whiteways Technical Centre, Enstone, Oxfordshire, United Kingdom",
                about = "The Enstone outfit holds a rich pedigree, having previously won championships as Benetton (with Michael Schumacher) and Renault (with Fernando Alonso). For the 2026 regulations, the French manufacturer restructured into a lean chassis specialist powered by customer Mercedes engines."
            )
            "williams" -> TeamAboutInfo(
                fullName = "Atlassian Williams F1 Team",
                base = "Station Road, Grove, Oxfordshire, United Kingdom",
                about = "A legendary heritage privateer founded by Sir Frank Williams that dominated the 1980s and 1990s with nine Constructors' Championships. Under Team Principal James Vowles, Williams is executing a modernized infrastructure rebuild, pairing Carlos Sainz with Alex Albon to bring the Grove squad back to regular podium contention."
            )
            "rb", "racing_bulls" -> TeamAboutInfo(
                fullName = "Visa Cash App Racing Bulls F1 Team",
                base = "Via Boaria 229, Faenza, Ravenna, Italy",
                about = "Red Bull's sister team (originally Minardi, then Toro Rosso and AlphaTauri) based in Italy with satellite facilities in the UK. VCARB serves as both a fierce midfield contender and the proving ground where elite Red Bull junior drivers sharpen their racecraft before stepping into top-tier machinery"
            )
            "audi" -> TeamAboutInfo(
                fullName = "Audi Revolut F1 Team",
                base = "Wildbachstrasse 9, Hinwil, Zurich, Switzerland",
                base2 = "Engines Base: Neuburg, Germany",
                about = "A historic full works entry for the German automotive giant, taking over the long-standing Sauber operation. Operating out of state-of-the-art facilities in Hinwil and building a bespoke power unit in Neuburg, Audi enters the sport with serious factory muscle and long-term championship ambitions."
            )
            "haas" -> TeamAboutInfo(
                fullName = "TGR Haas F1 Team",
                base = "Kannapolis, North Carolina, USA",
                base2 = "UK Base: Banbury, Oxfordshire",
                about = "F1’s pioneering American privateer, founded by Gene Haas in 2016. Haas operates an agile, multi-hub model across the US, UK, and Italy, utilizing Ferrari technical hardware alongside a collaboration with Toyota Gazoo Racing to punch well above their weight in the midfield."
            )
            "cadillac" -> TeamAboutInfo(
                fullName = "Cadillac F1 Team",
                base = "Fishers, Indiana, USA",
                base2 = "European Base: Silverstone, UK",
                about = "The all-new American outfit backed by General Motors, bringing the iconic Cadillac racing badge to the pinnacle of motorsport. Combining seasoned veteran drivers with an initial Ferrari power unit supply, Cadillac provides a high-profile US factory presence ahead of introducing their own bespoke GM engine."
            )
            else -> TeamAboutInfo(
                fullName = "NAME",
                base = "BASE",
                about = "ABOUT"
            )
        }
    }

    fun getDriverAbout(driverId: String?): DriverAboutInfo {
        return when (driverId?.lowercase()) {
            "norris" -> DriverAboutInfo(
                team = "McLaren Mastercard F1 Team",
                country = "Great Britain",
                number = "4",
                about = "The 2025 World Champion and McLaren's franchise anchor. Blending blistering qualifying speed with razor-sharp tire management, Norris joined McLaren in 2019 and spearheaded the team's return to the top step of the podium."
            )
            "piastri" -> DriverAboutInfo(
                team = "McLaren Mastercard F1 Team",
                country = "Australia",
                number = "81",
                about = "Known for his calm, calculating demeanour and ice-cold radio comms under pressure. Piastri entered F1 with back-to-back rookie titles in Formula 3 and Formula 2, quickly establishing himself as an uncompromising race winner and title contender."
            )
            "hamilton" -> DriverAboutInfo(
                team = "Scuderia Ferrari HP",
                country = "Great Britain",
                number = "44",
                about = "A 7-time World Champion and the statistical giant of Formula 1 with over 100 wins and pole positions. Hamilton’s historic move to Maranello unites the sport's most decorated modern driver with its most iconic racing team in pursuit of an elusive 8th title."
            )
            "leclerc" -> DriverAboutInfo(
                team = "Scuderia Ferrari HP",
                country = "Monaco",
                number = "16",
                about = "Ferrari's homegrown talisman and one of the purest single-lap qualifiers of his generation. Carrying the immense expectations of the Tifosi, the Monaco-born star combines explosive natural speed with unmatched bravery on street circuits."
            )
            "russell" -> DriverAboutInfo(
                team = "Mercedes-AMG PETRONAS F1 Team",
                country = "Great Britain",
                number = "63",
                about = "A relentlessly analytical and fiercely consistent racer nicknamed \"Mr. Saturday\" during his formative years at Williams. Russell stepped up to lead the Silver Arrows into the new regulatory era with formidable race pace and aggressive wheel-to-wheel combat."
            )
            "antonelli" -> DriverAboutInfo(
                team = "Mercedes-AMG PETRONAS F1 Team",
                country = "Italy",
                number = "12",
                about = "Mercedes' generational prodigy who bypassed F3 directly to F2 before earning a top-tier seat at just 18. Antonelli brings raw karting pedigree, remarkable car control in low-grip conditions, and Italy’s biggest hopes for an F1 champion in decades."
            )
            "max_verstappen" -> DriverAboutInfo(
                team = "Oracle Red Bull Racing",
                country = "Netherlands",
                number = "1",
                about = "A multiple World Champion and an uncompromising racing machine. Defined by fierce aggression, unmatched consistency, and surgical race execution, Verstappen is the benchmark of modern grand prix driving."
            )
            "hadjar" -> DriverAboutInfo(
                team = "Oracle Red Bull Racing",
                country = "France",
                number = "6",
                about = "The French-Algerian prospect promoted from Red Bull’s junior program. Dubbed \"Little Prost\" by Helmut Marko, Hadjar earned his place alongside Verstappen through fiery racecraft and bold overtaking maneuvers."
            )
            "alonso" -> DriverAboutInfo(
                team = "Aston Martin Aramco F1 Team",
                country = "Spain",
                number = "14",
                about = "The two-time World Champion and undisputed grandmaster of the grid with over two decades at the top. Alonso’s tactical racecraft, relentless fitness, and ability to out-drive machinery make him a constant podium threat."
            )
            "stroll" -> DriverAboutInfo(
                team = "Aston Martin Aramco F1 Team",
                country = "Canada",
                number = "18",
                about = "A seasoned veteran with multiple podiums and a pole position to his name. Stroll is particularly renowned for electric opening-lap starts and standout wet-weather driving."
            )
            "sainz" -> DriverAboutInfo(
                team = "Atlassian Williams F1 Team",
                country = "Spain",
                number = "55",
                about = "The \"Smooth Operator,\" celebrated for his sharp tactical brain, meticulous technical feedback, and strategic race management. A proven multi-time race winner, Sainz leads Williams' ambitious push back toward the front of the midfield."
            )
            "albon" -> DriverAboutInfo(
                team = "Atlassian Williams F1 Team",
                country = "Thailand",
                number = "23",
                about = "The defensive master who rebuilt his career into one of the most respected leaders on the grid. Albon pairs exceptional tire preservation with opportunistic qualifying performances."
            )
            "gasly" -> DriverAboutInfo(
                team = "BWT Alpine F1 Team",
                country = "France",
                number = "10",
                about = "A Grand Prix winner whose career is defined by resilience and tenacity. As Alpine's experienced team leader, Gasly brings aggressive race pace and consistency to the Enstone outfit."
            )
            "colapinto" -> DriverAboutInfo(
                team = "BWT Alpine F1 Team",
                country = "Argentina",
                number = "43",
                about = "Argentina's breakthrough talent who took the paddock by storm with bold moves and instant points finishes upon entering F1, earning a long-term full-time drive with Alpine."
            )
            "lawson" -> DriverAboutInfo(
                team = "Visa Cash App Racing Bulls F1 Team",
                country = "New Zealand",
                number = "30",
                about = "A tough, no-nonsense Kiwi racer who seized every reserve opportunity with fearless overtaking and immediate points finishes to lock down a full-time seat in the Red Bull stable."
            )
            "arvid_lindblad" -> DriverAboutInfo(
                team = "Visa Cash App Racing Bulls F1 Team",
                country = "Great Britain",
                number = "40",
                about = "Red Bull’s latest teenage sensation who rocketed through junior categories with dominant race victories, entering the grid as an aggressive, high-ceiling rookie."
            )
            "ocon" -> DriverAboutInfo(
                team = "TGR Haas F1 Team",
                country = "France",
                number = "31",
                about = "A Grand Prix winner built on grit, defensive stubbornness, and wheel-to-wheel tenacity. Ocon brings seasoned factory experience and sharp racecraft to lead the Haas garage."
            )
            "bearman" -> DriverAboutInfo(
                team = "TGR Haas F1 Team",
                country = "Great Britain",
                number = "87",
                about = "The Ferrari Academy standout who delivered a sensational points finish on debut in Saudi Arabia. Bearman combines clean race execution with elite natural raw pace."
            )
            "hulkenberg" -> DriverAboutInfo(
                team = "Audi Revolut F1 Team",
                country = "Germany",
                number = "27",
                about = "The ultimate midfield benchmark and qualifying specialist. With over 200 race starts, \"The Hulk\" brings unmatched technical feedback to spearhead Audi's factory debut."
            )
            "bortoleto" -> DriverAboutInfo(
                team = "Audi Revolut F1 Team",
                country = "Brazil",
                number = "5",
                about = "Managed by Fernando Alonso, Bortoleto clinched back-to-back F3 and F2 championships on his first attempts, bringing Brazil's next great title hopes to Audi's works project."
            )
            "perez" -> DriverAboutInfo(
                team = "Cadillac F1 Team",
                country = "Mexico",
                number = "11",
                about = " \"Checo,\" the king of tire management and street tracks. A multiple race winner with vast technical experience across multiple top teams, providing vital leadership for Cadillac's brand-new entry."
            )
            "bottas" -> DriverAboutInfo(
                team = "Cadillac F1 Team",
                country = "Finland",
                number = "77",
                about = "A 10-time Grand Prix winner and key contributor to five Constructors' Championships with Mercedes. Bottas brings elite qualifying speed and veteran development experience to Cadillac's maiden campaign."
            )
            else -> DriverAboutInfo(team = "", country = "", number = "", about = "Driver biography not available.")
        }
    }
}



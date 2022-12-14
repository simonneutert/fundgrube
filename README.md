# fundgrube

not a script i would show my mother-in-law and brag about it... but, ..., well, it does the job ð 

## why?

The Fundgrube does not offer sorting, filtering or anything you would call useful as a user. 

## what and how?

this tool curls through MediaMarkts public [Fundgrube](https://www.mediamarkt.de/de/data/fundgrube) API. 

**IMPORTANT** You need an active [telegram bot](https://core.telegram.org/bots/faq#how-do-i-create-a-bot) to use this script!

**AND** just the 5 newest pages are being tracked, if you don't set scope on certain outlet ids `export FUNDGRUBE_OUTLET_IDS="418,576,798"` !  
NOT setting this env will track all outlets.

Results are written to the filesystem utilizing Clojure's [edn data notation](https://github.com/edn-format/edn). The file with the last results is being copied and renamed, to serve as the source to identify changes (new postings/products in the Fundgrube ð¤«).

Identified changes (products / postings) are then being sent to the telegram bot ð¤

Here's an example of how a post to telegram could look like:

```text
Markt: Wiesbaden-Hasengarten
Produkt: PHILIPS 58PUS8546/12 LED TV (Flat, 58 Zoll / 146 cm, UHD 4K, SMART TV, Ambilight, Android TVâ¢ 10 (Q))

Preis: 700.00 â¬

Artikel 2734789
-
LED TV / 146cm/58Zoll / UHD 4K,
Ultra Resolution, Dolby Vision, HDR10+, Micro Dimming Pro, ISF-Farbmanagement,
2 x 10 W Full-Range-Lautsprecher,
Ausschalt-Timer, Lichtsensor, Bildabschaltung (bei Radiobetrieb), Eco-Modus
https://assets.mmsrg.com/is/166325/12975367df8e182e57044734f5165e190/c3/-/29336490249241a3925bcb97e235b830
```

## Run

after cloning this project, setup your environment variables. Please check the outlet ids of your interest given below!

1. install [babashka](https://github.com/babashka/babashka)
2. setup your environment 
   ```bash
   export FUNDGRUBE_TGRAM_API_KEY="1234567:ABCDEFGH-XYZ123"
   # you can find your channel/group/chat id by logging into web.telegram.org
   # selecting your channel/group/chat
   # BONUS: don't forget to prepend 100 ð¤·ââï¸ if your bot should post to a channel
   export FUNDGRUBE_TGRAM_CHANNEL="-100mychannelid"

   # !!! THIS IS OPTIONAL !!!
   export FUNDGRUBE_OUTLET_IDS="418,576,798"
   ```
3. `$ bb fundgrube.clj`
4. profit
5. bro down

### setup cronjobs

clone the product to your server, install babashka with the [Quickstart script](https://github.com/babashka/babashka#quickstart) or pick another method, but for Debian based, the script is the way to go. As it will put `bb` in your path and set everything up correctly. Borkdude is trustworthy, the day he goes BlackHat will be a day we all will remember. 

```bash
# have your environment variables stored/set in `.profile` ð¤
0,15,30,45 9-15 * * 1-6 cd /home/superman/fundgrube; source /home/superman/.profile; bb /home/superman/fundgrube/fundgrube.clj
```

## credits

inspired by: [RomanNess/fundgrube-crawler](https://github.com/RomanNess/fundgrube-crawler)

## Outlet lists

One way to update the Outlets, `outlets.json` contains the copied object from [fundgrube website](https://www.mediamarkt.de/de/data/fundgrube):

```bash 
cat outlets.json | \
   jq '.outlets | sort_by(.id) | .[] | select(.isActive == true) | "\(.id), \(.nameFull)"' | \
   jq -s '{outlets: .}' > outlets_jq.json
```

### Outlets (isActive == true) @2022-11-01

```json
{
  "outlets": [
    "32, MediaMarkt Fellbach (Alte B14)",
    "99, MediaMarkt Neunkirchen",
    "114, MediaMarkt Itzehoe",
    "135, MediaMarkt Emden",
    "182, MediaMarkt Siegen â Weidenau",
    "189, MediaMarkt Stade",
    "190, MediaMarkt Berlin-Mitte im Alexa",
    "234, MediaMarkt Henstedt-Ulzburg",
    "246, MediaMarkt Baden-Baden",
    "250, MediaMarkt DÃ¼sseldorf-MetrostraÃe",
    "251, MediaMarkt Castrop-Rauxel (im Castrop-Park)",
    "252, MediaMarkt Alzey (im Rheinhessen-Center)",
    "253, MediaMarkt Saarlouis (im Globus-Center)",
    "254, MediaMarkt Heidenheim (Schloss Arkaden)",
    "256, MediaMarkt Wetzlar",
    "257, MediaMarkt KÃ¶ln-Kalk (in den KÃ¶ln-Arcaden)",
    "259, MediaMarkt Bremen (Waterfront)",
    "260, MediaMarkt Hamburg-Altona (im Bahnhof Altona)",
    "261, MediaMarkt Peine",
    "262, MediaMarkt Berlin-Prenzlauer Berg (Ecke OstseestraÃe)",
    "263, MediaMarkt Karlsruhe - Ettlinger Tor",
    "264, MediaMarkt Deggendorf",
    "265, MediaMarkt Traunreut",
    "266, MediaMarkt Erfurt T.E.C. (im T.E.C.)",
    "267, MediaMarkt GieÃen",
    "268, MediaMarkt Neustadt an der WeinstraÃe",
    "269, MediaMarkt MÃ¶nchengladbach",
    "270, MediaMarkt Berlin-Steglitz Im Schloss",
    "271, MediaMarkt Duisburg-GroÃenbaum",
    "272, MediaMarkt Bad Neustadt",
    "273, MediaMarkt WÃ¼rzburg-DÃ¼rrbachau",
    "274, MediaMarkt Meerane",
    "275, MediaMarkt Buchholz in der Nordheide",
    "278, MediaMarkt Dietzenbach",
    "279, MediaMarkt Eschweiler",
    "280, MediaMarkt Papenburg",
    "376, MediaMarkt Neuss",
    "377, MediaMarkt DÃ¼sseldorf-Bilk Arcaden (Ecke BachstraÃe)",
    "378, MediaMarkt Holzminden",
    "381, MediaMarkt Erding (im West Erding Park)",
    "382, MediaMarkt Halberstadt",
    "401, MediaMarkt Regensburg",
    "402, MediaMarkt Stuttgart-Feuerbach",
    "403, MediaMarkt Bochum-Ruhrpark",
    "404, MediaMarkt Berlin-Waltersdorf",
    "405, MediaMarkt MÃ¼lheim",
    "406, MediaMarkt Berlin-Wedding",
    "407, MediaMarkt NÃ¼rnberg-Kleinreuth",
    "408, MediaMarkt Erlangen",
    "409, MediaMarkt Erfurt im ThÃ¼ringen Park",
    "410, MediaMarkt Ludwigshafen-Oggersheim (im Einkaufspark Oggersheim)",
    "411, MediaMarkt Wolfsburg",
    "412, MediaMarkt Berlin-Spandau",
    "414, MediaMarkt Kempten-AllgÃ¤u",
    "415, MediaMarkt MÃ¼nchen-Euroindustriepark",
    "416, MediaMarkt Aschaffenburg",
    "417, MediaMarkt Passau",
    "418, MediaMarkt Mainz",
    "419, MediaMarkt Egelsbach",
    "420, MediaMarkt Kaiserslautern",
    "421, MediaMarkt Freiburg",
    "422, MediaMarkt Reutlingen",
    "423, MediaMarkt Zwickau",
    "424, MediaMarkt Essen",
    "425, MediaMarkt Duisburg-Marxloh",
    "426, MediaMarkt Belm-OsnabrÃ¼ck",
    "427, MediaMarkt Paderborn (im SÃ¼dring-Einkaufscenter)",
    "428, MediaMarkt Bremerhaven Schiffdorf-Spaden",
    "429, MediaMarkt Rostock-Sievershagen",
    "430, MediaMarkt GÃ¼nthersdorf",
    "431, MediaMarkt Braunschweig",
    "432, MediaMarkt Mannheim-Neckarau",
    "434, MediaMarkt Herzogenrath",
    "436, MediaMarkt Rosenheim",
    "437, MediaMarkt Straubing",
    "438, MediaMarkt Hallstadt-Bamberg",
    "439, MediaMarkt WÃ¼rzburg",
    "440, MediaMarkt Schwentinental",
    "441, MediaMarkt Kiel",
    "442, MediaMarkt Hamburg-Nedderfeld",
    "443, MediaMarkt Pirmasens",
    "444, MediaMarkt Pforzheim",
    "445, MediaMarkt Porta Westfalica (Minden)",
    "446, MediaMarkt Bielefeld",
    "447, MediaMarkt Frankfurt-Nordwestzentrum",
    "448, MediaMarkt Fulda am Emaillierwerk",
    "449, MediaMarkt Halle-PeiÃen",
    "450, MediaMarkt Ludwigsburg",
    "453, MediaMarkt Recklinghausen",
    "454, MediaMarkt Bremen (Weserpark)",
    "455, MediaMarkt LÃ¼neburg",
    "456, MediaMarkt Neu-Ulm",
    "457, MediaMarkt Bischofsheim",
    "458, MediaMarkt SaarbrÃ¼cken-Saarbasar",
    "459, MediaMarkt Berlin-NeukÃ¶lln in den NeukÃ¶lln-Arcaden",
    "460, MediaMarkt Dresden-Kaufpark",
    "461, MediaMarkt MÃ¼nchen-Solln",
    "462, MediaMarkt Hildesheim",
    "463, MediaMarkt GÃ¶ttingen",
    "464, MediaMarkt Esslingen",
    "465, MediaMarkt Koblenz",
    "466, MediaMarkt Ingolstadt",
    "467, MediaMarkt Hof",
    "469, MediaMarkt Hamburg-Billstedt",
    "470, MediaMarkt Potsdam Im Sterncenter",
    "471, MediaMarkt Landshut",
    "472, MediaMarkt Bad DÃ¼rrheim",
    "473, MediaMarkt Ravensburg",
    "474, MediaMarkt KÃ¶ln-Marsdorf",
    "475, MediaMarkt LÃ¼beck",
    "476, MediaMarkt Hamburg-Wandsbek",
    "477, MediaMarkt Worms (im WEP)",
    "478, MediaMarkt Schwedt (Oder Center Schwedt)",
    "479, MediaMarkt Sindelfingen (im Breuningerland)",
    "480, MediaMarkt Dresden-ElbePark",
    "481, MediaMarkt Magdeburg-Pfahlberg",
    "482, MediaMarkt MÃ¼nchen-Pasing",
    "483, MediaMarkt Berlin-Biesdorf",
    "485, MediaMarkt Kassel (DEZ Kassel)",
    "486, MediaMarkt Plauen",
    "487, MediaMarkt Schweinfurt",
    "488, MediaMarkt Krefeld",
    "490, MediaMarkt Stralsund (im Strelapark EKZ)",
    "491, MediaMarkt Hameln",
    "492, MediaMarkt Chemnitz-Sachsenallee (im EKZ Sachsenallee)",
    "493, MediaMarkt NÃ¼rnberg-Langwasser",
    "494, MediaMarkt Offenburg",
    "495, MediaMarkt Lahr",
    "496, MediaMarkt MÃ¼nchen-Haidhausen",
    "497, MediaMarkt Leipzig-Paunsdorf (im Paunsdorf-Center)",
    "498, MediaMarkt Halstenbek",
    "500, MediaMarkt Neubrandenburg",
    "501, MediaMarkt Schwerin (im SchloÃpark-Center)",
    "502, MediaMarkt Konstanz",
    "503, MediaMarkt Ansbach",
    "505, MediaMarkt Bruchsal",
    "506, MediaMarkt Augsburg-Oberhausen",
    "507, MediaMarkt MÃ¼nster (im Gewerbegebiet SÃ¼d)",
    "508, MediaMarkt Chemnitz-RÃ¶hrsdorf (im Chemnitz-Center)",
    "509, MediaMarkt Flensburg",
    "510, MediaMarkt Berlin-Tegel in den Borsighallen",
    "511, MediaMarkt NeumÃ¼nster",
    "514, MediaMarkt Aschaffenburg-City (City-Galerie)",
    "515, MediaMarkt WÃ¼rzburg-City",
    "516, MediaMarkt Nordhorn",
    "517, MediaMarkt SchwÃ¤bisch GmÃ¼nd",
    "519, MediaMarkt Nagold",
    "521, MediaMarkt Kirchheim (im Nanz Center)",
    "522, MediaMarkt Schorndorf",
    "523, MediaMarkt SchwÃ¤bisch Hall",
    "525, MediaMarkt Crailsheim",
    "526, MediaMarkt Aalen",
    "527, MediaMarkt Heilbronn (am Europaplatz)",
    "529, MediaMarkt Backnang",
    "530, MediaMarkt Mosbach",
    "531, MediaMarkt Sinsheim",
    "532, MediaMarkt Eislingen",
    "534, MediaMarkt Karlsruhe - Bulach",
    "538, MediaMarkt Viernheim",
    "539, MediaMarkt KÃ¶ln City am Dom",
    "540, MediaMarkt Neuwied",
    "541, MediaMarkt Cottbus",
    "542, MediaMarkt Hannover-Vahrenheide",
    "543, MediaMarkt Jena",
    "544, MediaMarkt Albstadt",
    "545, MediaMarkt Hamburg-HummelsbÃ¼ttel",
    "546, MediaMarkt Heppenheim",
    "547, MediaMarkt Hannover-WÃ¼lfel",
    "548, MediaMarkt Coburg",
    "549, MediaMarkt Homburg (im Saarpfalz-Center)",
    "550, MediaMarkt Velbert",
    "551, MediaMarkt Bad Kreuznach",
    "552, MediaMarkt Main-Taunus-Zentrum",
    "553, MediaMarkt Zella-Mehlis",
    "554, MediaMarkt Trier  (Alleencenter direkt am Hbf)",
    "555, MediaMarkt Frankfurt-Borsigallee",
    "556, MediaMarkt Buxtehude",
    "558, MediaMarkt Augsburg-GÃ¶ggingen",
    "559, MediaMarkt Goslar",
    "560, MediaMarkt Rheine",
    "561, MediaMarkt Heide (im Gewerbegebiet Ost)",
    "562, MediaMarkt Landau",
    "563, MediaMarkt Weiterstadt",
    "564, MediaMarkt Greifswald",
    "565, MediaMarkt Rostock-Brinckmansdorf",
    "566, MediaMarkt Singen",
    "567, MediaMarkt Lingen",
    "568, MediaMarkt Wuppertal",
    "569, MediaMarkt Magdeburg-BÃ¶rdepark",
    "570, MediaMarkt Friedrichshafen (im Bodensee-Center)",
    "571, MediaMarkt Limburg",
    "572, MediaMarkt Ulm",
    "573, MediaMarkt SaarbrÃ¼cken auf den Saarterrassen",
    "574, MediaMarkt GÃ¼tersloh (ggÃ¼. der Feuerwehr)",
    "575, MediaMarkt NÃ¼rnberg-Schoppershof (im MERCADO Center)",
    "576, MediaMarkt Wiesbaden-Hasengarten",
    "577, MediaMarkt Heidelberg-Rohrbach (im Einkaufszentrum)",
    "578, MediaMarkt Berlin-SchÃ¶neweide im Zentrum SchÃ¶neweide",
    "579, MediaMarkt MÃ¼hldorf am Inn",
    "581, MediaMarkt Marburg",
    "582, MediaMarkt GrÃ¼ndau-Lieblos",
    "583, MediaMarkt Weilheim",
    "584, MediaMarkt Memmingen",
    "585, MediaMarkt DonauwÃ¶rth",
    "587, MediaMarkt Marktredwitz",
    "588, MediaMarkt Weiden",
    "589, MediaMarkt Dessau",
    "590, MediaMarkt Nienburg",
    "591, MediaMarkt Mannheim-Sandhofen",
    "593, MediaMarkt Idar-Oberstein (im Gewerbepark Nahetal)",
    "594, MediaMarkt Hamburg-Harburg",
    "595, MediaMarkt HÃ¼ckelhoven",
    "624, MediaMarkt Landsberg am Lech",
    "649, MediaMarkt Dresden Centrum-Galerie",
    "798, MediaMarkt Wiesbaden-Ãppelallee",
    "1172, MediaMarkt Hamburg-Oststeinbek",
    "1173, MediaMarkt Neutraubling",
    "1182, MediaMarkt Dortmund Indupark",
    "1183, MediaMarkt Elmshorn",
    "1184, MediaMarkt Wilhelmshaven",
    "1189, MediaMarkt Riesa",
    "1201, MediaMarkt Schwabach",
    "1212, MediaMarkt Stadthagen",
    "1229, MediaMarkt Leipzig HÃ¶fe am BrÃ¼hl",
    "1231, MediaMarkt Kulmbach",
    "1232, MediaMarkt Dorsten",
    "1233, MediaMarkt Lippstadt",
    "1247, MediaMarkt Neuburg an der Donau",
    "1248, MediaMarkt NÃ¶rdlingen",
    "1254, MediaMarkt Amberg",
    "1287, MediaMarkt Karlsfeld",
    "1307, MediaMarkt Gifhorn",
    "1309, MediaMarkt Bonn",
    "1334, MediaMarkt Eisenach",
    "1350, MediaMarkt Stuttgart Milaneo",
    "1392, MediaMarkt Neumarkt",
    "1397, MediaMarkt Leinfelden-Echterdingen",
    "1400, MediaMarkt Nordhausen",
    "1443, MediaMarkt Salzgitter",
    "1465, MediaMarkt Berlin-Hauptbahnhof",
    "1466, MediaMarkt BÃ¶blingen (im EKZ Mercaden)",
    "1775, MediaMarkt Dortmund-HÃ¶rde",
    "1776, MediaMarkt Weinheim",
    "1787, MediaMarkt Schweinfurt City",
    "1826, MediaMarkt Bornheim",
    "1841, MediaMarkt Burghausen",
    "1878, MediaMarkt Forchheim",
    "1964, MediaMarkt Leer",
    "6058, MediaMarkt Bergisch Gladbach"
  ]
}
```

package com.casezero.lastwitness;

final class GameData {
    static final class CaseFile {
        final String id,title,subtitle,victim,brief,killer,motive,method,reveal;
        final String[] suspects,evidence,timeline,deductions;
        CaseFile(String id,String title,String subtitle,String victim,String brief,String killer,String motive,String method,String reveal,
                 String[] suspects,String[] evidence,String[] timeline,String[] deductions){
            this.id=id; this.title=title; this.subtitle=subtitle; this.victim=victim; this.brief=brief; this.killer=killer;
            this.motive=motive; this.method=method; this.reveal=reveal; this.suspects=suspects; this.evidence=evidence;
            this.timeline=timeline; this.deductions=deductions;
        }
    }

    private static String[] a(String...v){ return v; }

    static CaseFile[] all(){ return new CaseFile[]{
        new CaseFile("CZ-001","THE LOCKED APARTMENT","A death that should be impossible","Elias Ward",
            "Greybridge, 11:46 PM. Financial investigator Elias Ward is found dead in Apartment 4C. The chain is latched from inside, the windows are locked, and patrol calls it suicide. One thing is wrong: somebody arranged the room to make you believe that.",
            "Victor Hale","Ward discovered Hale's blackmail network and hidden payments.","Hale drugged Ward, killed him before the apparent time of death, then used a monofilament trick through the transom to re-latch the chain and staged the watch.",
            "The room was never impossible. It was a performance. On your way home an anonymous photo arrives: you entering this building three nights earlier. You have never been here before.",
            a("Victor Hale|Neighbor / property broker|Calm, precise, knows too much","Anna Blake|Ward's former partner|Angry, financially exposed","Marcus Reed|Building superintendent|Had keys and lied about repairs","Noah Quinn|Investigative reporter|Was chasing Ward's files"),
            a("Broken Watch|Stopped at 9:17 PM. Impact marks are on the inside of the glass.","Second Glass|Cleaned too carefully; trace sedative remains in the stem.","Door Chain|Fresh abrasion and a nearly invisible fibre caught in the guide.","Ward's Phone|A deleted 9:08 PM call from a masked number.","CCTV Gap|Hall camera loses power from 9:11 to 9:24 PM.","Vent Fibre|Monofilament with graphite residue matching the chain track.","Prescription Bottle|Ward's medication is harmless; the sedative came from elsewhere.","Hotel Marlowe Receipt|Room 308, paid cash. Ward circled the number twice.","Ledger Fragment|Initials V.H. beside recurring cash transfers.","Rain Print|A partial wet sole mark inside despite the hall carpet being dry."),
            a("8:55 PM|Victor claims he was already home","9:08 PM|Ward receives masked phone call","9:11 PM|Hall CCTV loses power","9:17 PM|Broken watch is staged","9:24 PM|CCTV returns","9:31 PM|Neighbor reports a heavy sound"),
            a("Watch + Forensics|The displayed time of death was manufactured.","Chain + Fibre|The door could be re-latched from outside.","Ledger + Victor|Victor had a direct financial motive.")),

        new CaseFile("CZ-002","2:17 AM","She vanished after one unfinished message","Mara Voss",
            "Four days after Ward's murder, student journalist Mara Voss disappears. At 2:17 AM she sends a six-second voicemail: 'If I don't call back, look under—' The recording ends with three knocks and a train horn.",
            "Caleb Frost","Mara found proof that Frost was moving sealed evidence for a private network.","Frost lured Mara to a disused platform, took her phone, and hid her in a service tunnel while fabricating a rideshare trail.",
            "Mara is recovered alive. In her notebook is Ward's handwriting and one phrase: ROOM 308 IS NOT A ROOM.",
            a("Caleb Frost|Transit security contractor|Controls service access","Dr. Evelyn Shaw|Mara's lecturer|Deleted their last emails","Jonah Pike|Ex-boyfriend|Tracked her location","Lena Voss|Older sister|Withheld a family secret"),
            a("Voicemail 2:17|Three knocks, distant horn, metallic echo.","Transit Map|Only Platform 12 hears that horn at 2:17.","Fake Rideshare|Trip metadata was generated from a cloned device.","Locker Key|Stamped P12-S4.","Boot Dust|Iron-rich tunnel dust on Frost's boots.","Deleted Email|Mara warned Shaw about 'evidence moving after midnight'.","Service Log|Frost signed out the tunnel gate key.","Phone Clone|Mara's device ID appears in two places at once.","Ward Note|A copied page lists Frost beside a black square symbol.","Three Knocks|Maintenance signal used before opening the sealed tunnel door."),
            a("1:52 AM|Mara leaves campus","2:06 AM|Frost opens service gate","2:17 AM|Voicemail recorded","2:20 AM|Cloned phone starts fake ride","2:43 AM|Gate closes","3:01 AM|Frost clocks out"),
            a("Horn + Map|The voicemail was recorded at Platform 12.","Phone Clone + Ride|The rideshare alibi was fabricated.","Service Log + Dust|Frost entered the tunnel that night.")),

        new CaseFile("CZ-003","THE PASSENGER","A crash with one occupant too many","Daniel Cross",
            "A sedan crashes below the Greybridge viaduct. Driver Daniel Cross dies at the scene. Police record one occupant. Dashcam audio contains a second seatbelt chime, a whispered argument, and a door closing seven minutes before impact.",
            "Silas Wynn","Cross was transporting a drive containing payment records linked to the network.","Wynn rode hidden in the rear, forced Cross to divert, removed the drive, then sabotaged the steering sensor before leaving at a camera blind spot.",
            "The recovered drive contains hotel floor plans. Room 308 is marked as an internal evidence archive, not a guest room.",
            a("Silas Wynn|Corporate fixer|Claims never to have met Cross","Tessa Cole|Cross's fiancée|Beneficiary of insurance","Officer Grant Bell|First responder|Moved evidence","Owen Mercer|Mechanic|Serviced the car that morning"),
            a("Dashcam Audio|Second seatbelt chime and a low male voice.","Rear Fabric|Fibres disturbed behind the driver's seat.","Steering Sensor|Connector deliberately loosened after servicing.","Blind Spot Map|Seven-minute road segment has no public cameras.","Missing Drive|Cross texted 'I have the archive' before leaving.","Toll Record|Vehicle mass reading is inconsistent with one occupant.","Wynn Receipt|Fuel purchase two blocks from the blind spot.","Door Audio|Rear door closes before the final drive segment.","Mechanic Photo|Sensor connector was intact at 10:11 AM.","Blood Trace|Tiny transfer on rear latch matches Wynn's old cut in booking records."),
            a("10:11 AM|Car leaves mechanic intact","8:42 PM|Cross texts about archive","9:03 PM|Second belt chime","9:18 PM|Car enters blind spot","9:25 PM|Rear door closes","9:39 PM|Steering fails at viaduct"),
            a("Audio + Toll|There was a second passenger.","Mechanic + Sensor|The failure was sabotage after servicing.","Blind Spot + Wynn|Wynn had time and location to exit unseen.")),

        new CaseFile("CZ-004","ROOM 308","The hotel has no Room 308","Iris Bell",
            "Archivist Iris Bell is found dead inside a sealed service room at Hotel Marlowe. The elevator has no third-floor button labelled 308, and the official floor plan ends at 307.",
            "Marcus Veil","Bell planned to release the archive proving years of evidence laundering.","Veil used the staff lift and a master maintenance credential, killed Bell in the hidden archive, then falsified the electronic access log by replaying an old credential packet.",
            "Behind the archive wall is a photograph of senior Greybridge officers. One face is scratched out. Another belongs to Deputy Chief Rowan Creed.",
            a("Marcus Veil|Hotel security director|Controls access logs","Claire Marlowe|Hotel owner|Family built the hidden room","Ezra Knox|Night auditor|Sold access information","Detective Paul Rusk|Internal affairs|Arrived before the body call"),
            a("Hidden Floor Plan|A maintenance layer shows an unnumbered archive behind 307.","Replay Log|Access timestamps repeat an impossible encryption nonce.","Staff Lift Grease|Fresh lubricant transfer on Veil's sleeve.","Ward Receipt|The Case 01 receipt points to this archive.","Bell Recorder|She says 'Marcus is here' before static.","Master Credential|Veil's token generated the replay packet.","Archive Dust|One shelf was cleared minutes before death.","Scratched Photo|Senior officers pose beside the network symbol.","Fire Door Sensor|Opened without guest elevator activity.","Removed Box|Label: CZ / WITNESS PROGRAM / 1999."),
            a("10:02 PM|Bell enters hotel","10:14 PM|Staff lift moves to hidden level","10:19 PM|Recorder captures Bell","10:23 PM|Replay access packet appears","10:30 PM|Fire door sensor trips","10:47 PM|Body call is logged"),
            a("Plan + Lift|Room 308 is a hidden service archive.","Replay + Credential|The access log was forged by Veil.","Recorder + Veil|Bell directly identifies her visitor.")),

        new CaseFile("CZ-005","THE LAST WITNESS","Every case was designed to find you","Adrian Vale",
            "The trail ends beneath Greybridge's abandoned courthouse. Adrian Vale, the last surviving witness from a 1999 covert program, asks for protection. Minutes later the lights fail and he is murdered inside a monitored safe room.",
            "Deputy Chief Rowan Creed","Creed ran the old program and used your investigation to locate the witnesses and missing archive.","Creed exploited police emergency access, looped the safe-room feed, poisoned Vale through a replacement oxygen canister, and attempted to frame your detective identity as the final compromised witness.",
            "Creed is exposed, but the final archive reveals why your photograph appeared in Case 01: your identity was copied from a child witness record. Someone has been editing your history for years. The Last Witness was never Vale. It was you.",
            a("Rowan Creed|Deputy chief|Your superior throughout the investigation","Paul Rusk|Internal affairs|Has access to sealed records","Nora Grey|Forensic director|Handled every toxicology report","Adrian Vale|The witness|His own history contains contradictions"),
            a("Looped Feed|Exactly 94 seconds repeat during the blackout.","Oxygen Canister|Seal replaced; fast-acting toxin in regulator.","Emergency Credential|Creed's command token opens the sublevel.","1999 Photo|Creed appears beside the original witness team.","Case Zero File|Your chosen detective name is attached to a redacted child record.","Dispatch Gap|Creed orders every nearby unit away for six minutes.","Archive Index|Victims Ward, Mara, Cross and Bell all touched the same program.","Voice Match|Anonymous calls from Case 01 match Creed's archived briefing voice.","Chain of Custody|Evidence from all four cases passed through Creed's office.","Last Envelope|Addressed to you twenty-seven years before tonight."),
            a("11:40 PM|Vale enters safe room","11:46 PM|Creed redirects units","11:49 PM|Feed begins 94-second loop","11:50 PM|Emergency credential opens sublevel","11:52 PM|Oxygen alarm triggers","11:56 PM|Creed reports Vale dead"),
            a("Feed + Dispatch|The blackout was operational, not accidental.","Canister + Credential|Creed had the means and access to kill Vale.","Archive + Voice|Creed connects and manipulated all five cases."))
    }; }
}

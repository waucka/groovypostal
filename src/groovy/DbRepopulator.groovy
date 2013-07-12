package groovypostal

import java.util.zip.ZipFile

class DbRepopulator {
	static final validStates = [
		AL:"Alabama",
		AK:"Alaska",
		AZ:"Arizona",
		AR:"Arkansas",
		CA:"California",
		CO:"Colorado",
		CT:"Connecticut",
		DE:"Delaware",
		FL:"Florida",
		GA:"Georgia",
		HI:"Hawaii",
		ID:"Idaho",
		IL:"Illinois",
		IN:"Indiana",
		IA:"Iowa",
		KS:"Kansas",
		KY:"Kentucky",
		LA:"Louisiana",
		ME:"Maine",
		MD:"Maryland",
		MA:"Massachusetts",
		MI:"Michigan",
		MN:"Minnesota",
		MS:"Mississippi",
		MO:"Missouri",
		MT:"Montana",
		NE:"Nebraska",
		NV:"Nevada",
		NH:"New Hampshire",
		NJ:"New Jersey",
		NM:"New Mexico",
		NY:"New York",
		NC:"North Carolina",
		ND:"North Dakota",
		OH:"Ohio",
		OK:"Oklahoma",
		OR:"Oregon",
		PA:"Pennsylvania",
		RI:"Rhode Island",
		SC:"South Carolina",
		SD:"South Dakota",
		TN:"Tennessee",
		TX:"Texas",
		UT:"Utah",
		VT:"Vermont",
		VA:"Virginia",
		WA:"Washington",
		WV:"West Virginia",
		WI:"Wisconsin",
		WY:"Wyoming"
]

  def parseFile(validStates, states, filename) {
		def zipFile = new ZipFile(new File(filename))
		zipFile.entries().each{
			if(it.name == 'US.txt') {
				zipFile.getInputStream(it).eachLine {
					def parts = it.split("\t")
					if(parts.length >= 5) {
						def statecode = parts[4]
						if(statecode in states) {
							def zip = parts[1]
							def name = parts[2]
							def statename = validStates[statecode]
							new PostalCode(state:states[statecode], code:zip, name:name).save()
						}
					}
				}
			}
		}
	}

  def reload() {
		def states = [:]
		validStates.each{states.put(it.key, null)}

		PostalCode.executeUpdate('delete from PostalCode')
		State.executeUpdate('delete from State')
		DbStatus.executeUpdate('delete from DbStatus')
		new DbStatus(ok: false).save()

		validStates.each{
			def newstate = new State(code:it.key, name:it.value)
			states[it.key] = newstate
			newstate.save()
		}

		def tmpdirname = "/tmp"
		try {
			//Grails uses an older version of Groovy that doesn't have File.createTempDir()
			println("Downloading to " + tmpdirname + "...")
			def command = """wget -O $tmpdirname/US.zip http://download.geonames.org/export/zip/US.zip"""
			def proc = command.execute()
			proc.waitFor()

			if(proc.exitValue() == 0) {
				println("Downloaded file!")
				parseFile(validStates, states, tmpdirname + "/US.zip")
			} else {
				println("Failed to download file!")
			}
			DbStatus.getAll().each{
				it.ok = true
				it.save()
			}
		} finally {
			new File(tmpdirname + "/US.zip").delete()
		}
	}
}

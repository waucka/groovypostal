package groovypostal

class State {
	static hasMany = [postalCodes: PostalCode]
	String code
	String name
	static constraints = {
		code unique: true
	}
}

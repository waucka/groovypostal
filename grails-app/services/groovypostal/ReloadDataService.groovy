package groovypostal

class ReloadDataService {
	def executorService
	def reloadData() {
		executorService.submit({
														 def dbr = new DbRepopulator()
														 dbr.reload()
													 })
	}
}

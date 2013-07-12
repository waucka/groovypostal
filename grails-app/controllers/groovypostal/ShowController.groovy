package groovypostal

class ShowController {
	def reloadDataService

	def tagcloud() {
		def MIN_FONT_SIZE = 8
		def MAX_FONT_SIZE = 50

		def tagcloudtags = []
		def minzips = Integer.MAX_VALUE
		def maxzips = Integer.MIN_VALUE

		State.findAll([sort: "name", order: "asc"]).each{
			int numzips = PostalCode.countByState(it)
			tagcloudtags.add(new TagCloudTag(title:it.name, size: numzips, code:it.code))
			if(numzips < minzips) {
				minzips = numzips
			}
			if(numzips > maxzips) {
				maxzips = numzips
			}
		}

		int fsdiff = MAX_FONT_SIZE - MIN_FONT_SIZE
		int zipcountdiff = maxzips - minzips
		double scale = fsdiff / zipcountdiff

		tagcloudtags.each {
			it.size = 8 + it.size * scale
		}

		render(view: "tagcloud", model: [tags: tagcloudtags, dbok: DbStatus.countByOk(true) > 0])
	}

	def listzips() {
		def state = State.findByCode(params.id)

		render(view: "listzips", model: [zips: PostalCode.findAllByState(state), statename: state.name])
	}

	def reloaddata() {
		render(view: "reloaddata")
	}

	def reallyreload() {
		//def dbr = new DbRepopulator()
		//dbr.reload()
		reloadDataService.reloadData()
		redirect(action: "tagcloud")
	}
}

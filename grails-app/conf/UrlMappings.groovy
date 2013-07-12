class UrlMappings {

	static mappings = {
		"/$controller/$action?/$id?"{
			constraints {
				// apply constraints here
			}
		}

		"/"(controller:'show', action:"/tagcloud")
		"500"(view:'/error')
	}
}

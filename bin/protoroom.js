Object.prototype.keys = function() {
	var keys = []
	for (var k in this) {
		if (!k.startsWith('_') && this.hasOwnProperty(k)) {
			keys.push(k)
		}
	}
	return keys
}
function Store(content) {
	this.content = content || {}
}
Store.prototype.store = function(content) {
	for ( var i in content) {
		if (content.hasOwnProperty(i)) {
			this.content[i] = content[i]
		}
	}
}
Store.prototype.dump = function(key) {
	if (typeof key !== 'undefined') {
		return _dump(this.content[key], '  ')
	}
	return '(' + _dump(this.content) + ')'
}
Store.prototype.list = function() {
	var list = []
	for (var k in this.content) {
		if (this.content.hasOwnProperty(k)) {
			list.push(k)
		}
	}
	return list
}
Store.prototype.has = function(key) {
	return !!this.content[key]
}

var _store = new Store

function _dump(data, pretty) {
	switch (typeof data) {
	case 'string':
		return '"' + data + '"'
	case 'number':
	case 'boolean':
		return String(data)
	case 'function':
		return data.toString()
	case 'object':
		var dump
		if (data.constructor.name === 'Array') { // FIXME : ???
			dump = '[' + (pretty ? '\n' : '')
			for (var i = 0; i < data.length; i++) {
				if (i > 0) {
					dump += ',' + (pretty ? '\n' : '')
				}
				var sub = _dump(data[i], pretty)
				if (pretty) sub = sub.replace(/(^|\n)/g, '$1' + pretty)
				dump += sub
			}
			dump += (pretty ? '\n' : '') + ']'
		} else {
			dump = '{' + (pretty ? '\n' : '')
			var comma
			for ( var i in data) {
				if (comma) {
					dump += ',' + (pretty ? '\n' : '')
				} else {
					comma = true
				}
				if (data.hasOwnProperty(i)) {
					var sub = _dump(i) + (pretty ? ' : ' : ':') + _dump(data[i], pretty)
					if (pretty) sub = sub.replace(/(^|\n)/g, '$1' + pretty)
					dump += sub
				}
			}
			dump += (pretty ? '\n' : '') + '}'
		}
		return dump
	}
}
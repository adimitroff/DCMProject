// Object for feed item
function RssFeedItem()
{
	this.title = '';
	this.description = '';
	this.mediaType = '';
	this.mediaUrl = '';
	this.pubDate = new Date();
};


function RssFeed(xmlRssFeed, idElemFeed)
{
	// public properties
	this.itemsList = [];
	this.elemFeedItem = document.getElementById(idElemFeed);
	this.elemImage = this.elemFeedItem.querySelector('[title="rssImage"]');
	this.elemTitle = this.elemFeedItem.querySelector('[title="rssTitle"]');
	this.elemDesc = this.elemFeedItem.querySelector('[title="rssDesc"]');
	
	// Parse xml rss feed 
	var parser = new DOMParser();
    var xmlDoc = parser.parseFromString(xmlRssFeed, 'text/xml');
    var xmlItems = xmlDoc.getElementsByTagName('item');
    for(var i = 0; i < xmlItems.length; i++) {
    	// Parse chanel item
    	if(xmlItems[i].parentNode.nodeName == "channel") { // check parrent node is chanel
    		var feedItem = new RssFeedItem();
    		var xmlItemNodes = xmlItems[i].childNodes;
    		for(var j = 0; j < xmlItemNodes.length; j++) {
    			
    			switch(xmlItemNodes[j].nodeName) {
    			case 'title': {
    				feedItem.title = xmlItemNodes[j].childNodes[0].nodeValue;
    				break;
    			}
    			case 'description': {
    				feedItem.description = xmlItemNodes[j].childNodes[0].nodeValue;
    				break;
    			}
    			case 'pubDate': {
    				feedItem.pubDate = new Date(xmlItemNodes[j].childNodes[0].nodeValue);
    				break;
    			}
    			case 'enclosure': {
    				var attributes = xmlItemNodes[j].attributes;
    				var type = attributes.getNamedItem("type").nodeValue;
    				if(type.indexOf("image/") == 0) {
    					feedItem.mediaType = type;
    					feedItem.mediaUrl = attributes.getNamedItem("url").nodeValue;
    				}
    				break;
    			}
    			}
    		}
    		// Push to list of items
    		this.itemsList.push(feedItem);
    	}
    }
    App.logInfo("feeds: " + this.itemsList.length);
    
    return this;
};

RssFeed.STATUS_STOPED = -2;
RssFeed.STATUS_PLAYING = -1;

RssFeed.prototype = 
{		
		currentPlayIdx: RssFeed.STATUS_STOPED,
		durationMs: 10000,
		
		status: function()
		{
			if(this.currentPlayIdx >= RssFeed.STATUS_PLAYING) {
				return RssFeed.STATUS_PLAYING;
			}
			return RssFeed.STATUS_STOPED;
		},

		play: function(durationMs)
		{
			if(this.status() == RssFeed.STATUS_PLAYING) {
				this.stop();
			}
			App.logInfo("RssFeed.play()");
			this.durationMs = durationMs;
			this.currentPlayIdx = RssFeed.STATUS_PLAYING; 
			this.playNext();
			this.elemFeedItem.classList.remove("hidden");
		},

		playNext: function()
		{
			if(this.currentPlayIdx == RssFeed.STATUS_STOPED || this.itemsList.length == 0) {
				return;
			}
			this.currentPlayIdx++;
			if(this.currentPlayIdx == this.itemsList.length) {
				// Repeat from first element
				this.currentPlayIdx = 0;
			}

			var feedItem = this.itemsList[this.currentPlayIdx];
			this.elemTitle.innerHTML = feedItem.title;
			this.elemDesc.innerHTML = feedItem.description;
			if(feedItem.mediaType.indexOf("image/") == 0) {
				this.elemImage.src = feedItem.mediaUrl;
			} else {
				this.elemImage.src = "";
			}

			this.nextTimeout = setTimeout(this.playNext.bind(this), this.durationMs);
		},

		stop: function() {
			App.logInfo("RssFeed.stop()");
			if(this.nextTimeout) {
				clearTimeout(this.nextTimeout);
			}
			this.currentPlayIdx = RssFeed.STATUS_STOPED; // stop play
			this.elemFeedItem.classList.add("hidden");
		}
};









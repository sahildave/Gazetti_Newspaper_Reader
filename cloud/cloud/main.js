var xmlreader = require('cloud/xmlreader.js');
var Buffer = require('buffer').Buffer;
 
var HinduNat = Parse.Object.extend("HinduNat");
var ToiInter = Parse.Object.extend("ToiInter");
var NewsArticle = Parse.Object.extend("newsArticle");
var DoubleObjects = Parse.Object.extend("DoubleObjects");
var FreshNewsArticle = Parse.Object.extend("freshNewsArticle");
var th_toi_combined = Parse.Object.extend("th_toi_combined");
var temp_class = Parse.Object.extend("temp_class");
var DateExperiment = Parse.Object.extend("dateExp");
 
var toi_data = Parse.Object.extend("toi_data");
var hindu_data= Parse.Object.extend("hindu_data");
var tie_data = Parse.Object.extend("tie_data");
var fp_data = Parse.Object.extend("fp_data");
 
Parse.Cloud.job("job_hindu_national", function (request, response) {
    return Parse.Cloud.httpRequest({
        url: 'http://www.thehindu.com/news/national/?service=rss'
    }).then(function(httpResponse) {
        var someXml = httpResponse.text;
        xmlreader.read(someXml, function (err, res){
            if(err) {
                response.error("Error " +err);
                return console.log(err);
            }   
                  
            var listArray = [];
            res.rss.channel.item.each(function (i, item){
                var hinduNat = new HinduNat();
                hinduNat.set("link", item.link.text());
                hinduNat.set("title", item.title.text());
                hinduNat.set("pubDate", item.pubDate.text());
                listArray.push(hinduNat);
            });
              
            var promises = [];
            Parse.Object.saveAll(listArray, {
                    success: function(objs) {
                        promises.push(objs);
                        console.log("SAVED ALL!");
                    },
                    error: function(error) { 
                        console.log("ERROR WHILE SAVING - "+error);
                    }   
                });
            return Parse.Promise.when(promises);        
              
        });
    }).then(function() {
            response.success("Saving completed successfully.");
            },function(error) {
            response.error("Uh oh, something went wrong.");
    });
});
  
Parse.Cloud.beforeSave("HinduNat", function(request, response) {
    //console.log("in beforeSave");
    var query = new Parse.Query(HinduNat);
    var linkText = request.object.get("link")
    var titleText = request.object.get("title");
    query.equalTo("link", linkText);
    query.first({
      success: function(object) {
        //console.log("in query");
        if (object) {
            //console.log("found");
            if(object.get('title')!==titleText){
                //console.log("title not same");
                object.set("title", titleText);
                response.success();
            }
            else{
                //console.log("title same");
                response.error();
            }
        } else {
            //console.log("not found");
            response.success();
        }
      },
      error: function(error) {
        response.error();
      }
    });
});
  
//Get urls from all the newspaper-category pairs
Parse.Cloud.job("job_get_data_for_all_newspapers", function (request, response) {     // 1 API REQUEST
    //news_cat class contains the newspaper_category pairs 
    var newsCatList = Parse.Object.extend("news_cat");
    var query = new Parse.Query(newsCatList);                                                            
    query.find().then(function(list){                                                                       // 1 API REQUEST
        console.log("Successfully retrieved " + list.length);
          
        //create array and use Promise.when to wait till it is filled
        var promisesGetNP = [];
        for (var i = 0; i < list.length; i++) { 
            promisesGetNP.push(getDataForNewspaper(list[i].get("newspaper_id_column"), list[i].get("category_id_column"), list[i].get("url_column")));
        }
        return Parse.Promise.when(promisesGetNP);
          
    }).then (function(result) {
            console.log("done job");
            response.success("Saving completed successfully.");
        },function(error) {
            console.log("job not done, error - "+error.message);
            response.error("Uh oh, something went wrong.");
        }
    );
}); 
  
//get httpResponse from a url
function getDataForNewspaper(newspaperId, catId , feedUrl){
    console.log("getData NEW & CAT ID - " + newspaperId + ", "+catId+ " feedUrl "+feedUrl);
      
    //create array and use Promise.when to wait till it is filled
    var promisesGetData = []; 
    return Parse.Cloud.httpRequest({
        url: feedUrl
    }).then(function(httpResponse){
        promisesGetData.push(processDataForNewspaper(httpResponse, newspaperId, catId));
        return Parse.Promise.when(promisesGetData);
          
    }).then(function() {
        console.log("Done getData? ");
        return Parse.Promise.as("Got Data");
    });
}
 
function processDataForNewspaper(httpResponse, newspaperId, catId){
    console.log("processData NEW & CAT ID - " + newspaperId + ", "+catId);
    someXml = httpResponse.text
      
    //using xmlreader.js and sax.js to parse xml into text
    xmlreader.read(someXml, function (err, res){
        if(err) {
            return console.log(err);
        }   
          
        //create a new array and push objects with data in it       
        var listArray = [];
        res.rss.channel.item.each(function (i, item){
            var newsArt = new FreshNewsArticle();
            newsArt.set("link", item.link.text());
            newsArt.set("title", item.title.text());
            newsArt.set("pubDate", item.pubDate.text());
            newsArt.set("newspaper_id",newspaperId);
            newsArt.set("cat_id",catId);
             
            listArray.push(newsArt);
        });
 
        //create a new promises array
        var promises = new Array();
        Parse.Object.saveAll(listArray, {                                                                   // 1 API REQUEST
                success: function(objs) {
                    promises.push(objs);
                    console.log("SAVED ALL!");
                },
                error: function(error) { 
                    console.log("ERROR WHILE SAVING - "+error.message);
                }   
            });
        return Parse.Promise.when(promises);        
    });
}
  
Parse.Cloud.beforeSave("freshNewsArticle", function(request, response) {
     
    var linkText = request.object.get("link")
    var titleText = request.object.get("title");
 
    var query = new Parse.Query(FreshNewsArticle);
    query.equalTo("title", titleText);
    query.limit(1000);
    query.first({
        success: function(object) {
            // console.log("in query");
            if (object) {
                // console.log("found");
                response.error();
                 
            } else {
                // console.log("not found");
                response.success();
            }
        },
        error: function(error) {
        response.error();
      }
    });
     
     
});
 
//Get urls from all the newspaper-category pairs
Parse.Cloud.job("job_get_data_for_all_newspapers_test", function (request, response) {     // 1 API REQUEST
    //news_cat class contains the newspaper_category pairs 
    var newsCatList = Parse.Object.extend("news_cat_2");
    var query = new Parse.Query(newsCatList);                                                            
    query.find().then(function(list){                                                                       // 1 API REQUEST
        console.log("Successfully retrieved " + list.length);
        //create array and use Promise.when to wait till it is filled
        var promisesGetNP = [];
        for (var i = 0; i < list.length; i++) { 
            promisesGetNP.push(getDataForNewspaperTest(list[i].get("newspaper_id_column"), list[i].get("category_id_column"), list[i].get("url_column")));
        }
        return Parse.Promise.when(promisesGetNP);
          
    }).then (function(result) {
            console.log("done job");
            response.success("Saving completed successfully.");
        },function(error) {
            console.log("job not done, error - "+error.message);
            response.error("Uh oh, something went wrong.");
        }
    );
}); 
  
//get httpResponse from a url
function getDataForNewspaperTest(newspaperId, catId , feedUrl){
    console.log("getData NEW & CAT ID - " + newspaperId + ", "+catId+ " feedUrl "+feedUrl);
      
    //create array and use Promise.when to wait till it is filled
    var promisesGetData = []; 
    return Parse.Cloud.httpRequest({
        url: feedUrl
    }).then(function(httpResponse){
        promisesGetData.push(processDataForNewspaperTest(httpResponse, newspaperId, catId));
        return Parse.Promise.when(promisesGetData);
          
    }).then(function() {
        console.log("Done getData? ");
        return Parse.Promise.as("Got Data");
    });
}
 
function processDataForNewspaperTest(httpResponse, newspaperId, catId){
    console.log("processData NEW & CAT ID - " + newspaperId + ", "+catId);
    someXml = httpResponse.text
      
    //using xmlreader.js and sax.js to parse xml into text
    xmlreader.read(someXml, function (err, res){
        if(err) {
            return console.log(err);
        }   
          
        //create a new array and push objects with data in it       
        var listArray = [];
        res.rss.channel.item.each(function (i, item){
            var newsArt = new temp_class();
            newsArt.set("link", item.link.text());
            newsArt.set("title", item.title.text());
            newsArt.set("pubDate", item.pubDate.text());
            newsArt.set("newspaper_id",newspaperId);
            newsArt.set("cat_id",catId);
             
            listArray.push(newsArt);
        });
 
        //create a new promises array
        var promises = new Array();
        Parse.Object.saveAll(listArray, {                                                                   // 1 API REQUEST
                success: function(objs) {
                    promises.push(objs);
                    console.log("SAVED ALL!");
                },
                error: function(error) { 
                    console.log("ERROR WHILE SAVING - "+error.message);
                }   
            });
        return Parse.Promise.when(promises);        
    });
}
  
Parse.Cloud.beforeSave("temp_class", function(request, response) {
     
    var linkText = request.object.get("link")
    var titleText = request.object.get("title");
 
    var query = new Parse.Query(temp_class);
    query.equalTo("title", titleText);
    query.limit(1000);
    query.first({
        success: function(object) {
            // console.log("in query");
            if (object) {
                // console.log("found");
                response.error();
                 
            } else {
                // console.log("not found");
                response.success();
            }
        },
        error: function(error) {
        response.error();
      }
    });
     
     
});
 
 
 
Parse.Cloud.job("DeleteDuplicate", function(request, status) {
 
    var query = new Parse.Query(temp_class);
    var limit = 1000;
    query.limit(limit);
    //query.ascending("pubDate");
    var deleteList = [];
    query.find().then(function(results) {
         
        console.log("Successfully retrieved " + results.length);
        for( var i=0; i<results.length-1; i++){
            // console.log("i -"+i+", "+results[i].get("title"));
            var checkT1 = results[i].get("title");
            var checkL1 = results[i].get("link"); 
            for( var j=i+1; j<results.length; j++){
                // console.log("j - "+j+", "+results[j].get("title"));
                var checkT2 = results[j].get("title");
                var checkL2 = results[j].get("link"); 
                // console.log("yes - ? "+(checkT1 === checkT2));
                if(checkT1 === checkT2){
                    console.log("breaking at i = "+i);
                    console.log("i -"+i+", "+results[i].get("title"));
                    deleteList.push(results[i]);
                    break;
                }
            }
        }
        console.log("Deleting " + deleteList.length);
    }).then(function(){
        return Parse.Object.destroyAll(deleteList);
    }).then(function(success){
        status.success("Deleted " + deleteList.length);
        }, function(error) {
        status.error("Error: " + error.code + " " + error.message);
    });
});
 
Parse.Cloud.job("findAll", function(request, status) {
    var result = [];    
    var processCallback = function(res) {
        console.log("processCallback res length + "+res.length);
        result = result.concat(res);
        console.log("after concat "+result.length+" , "+res.length);
        if (res.length === 1000) {
          process(res[res.length-1].id);
          return;
        }
        console.log("result.length "+ result.length);
        // do something about the result, result is all the object you needed.
        status.success("final length " + result.length);
    }
    var process = function(skip) {
        console.log("process "+skip);
        var query = new Parse.Query(FreshNewsArticle);
        if (skip){
            console.log("in if");
            query.greaterThan("objectId", skip);
        }
        query.limit(1000);
        query.ascending("objectId");
        query.find().then(function querySuccess(res) {
              processCallback(res);
        }, function queryFailed(reason) {
              status.error("query unsuccessful, length of result " + result.length + ", error:" + error.code + " " + error.message);
        });
    }
    process(false);
});
 
Parse.Cloud.job("job_dateParse", function (request, response) {
     
    //final format 2014-05-02T11:15:21.568Z _____ May 02, 2014, 11:15
     
    var dateexp = new DateExperiment();
     
    var pubDateHindu= 'Fri, 2 May 2014 12:50:04 +0530';
    var pubDateTOI = 'Fri, 02 May 2014 12:50:04 GMT';
     
    var hindu = new Date(pubDateHindu);
    var toi = new Date(pubDateTOI);
    var parse = new Date ('2014-05-02T12:50:04Z');
     
    dateexp.set("hindu", hindu);
    dateexp.set("toi", toi);
    dateexp.set("parse", parse);
     
    console.log("hindu "+hindu);
    console.log("toi "+toi);
    console.log("parse "+parse);
     
    dateexp.save(null, {
        success: function(dateexp) {
        response.success('New object created with objectId: ' + dateexp.id);
        },
        error: function(dateexp, error) {
        response.error('Failed to create new object, with error code: ' + error.description);
        }
    });
});
 
/////////////////////////////////// HINDU ////////////////////////////////
 
 
Parse.Cloud.job("get_data_hindu_all", function (request, response) {     // 1 API REQUEST
    //news_cat class contains the newspaper_category pairs 
    var newsCatList = Parse.Object.extend("hindu_url");
    var query = new Parse.Query(newsCatList);                                                            
    query.find().then(function(list){                                                                       // 1 API REQUEST
        console.log("Successfully retrieved " + list.length);
        //create array and use Promise.when to wait till it is filled
        var promisesGetNP = [];
        for (var i = 0; i < list.length; i++) { 
            promisesGetNP.push(getDataForHindu(list[i].get("newspaper_id_column"), list[i].get("category_id_column"), list[i].get("url_column")));
        }
        return Parse.Promise.when(promisesGetNP);
          
    }).then (function(result) {
            console.log("done Hindu job");
            response.success("Saving completed successfully.");
        },function(error) {
            console.log("Hindu job not done, error - "+error.message);
            response.error("Uh oh, something went wrong.");
        }
    );
}); 
  
//get httpResponse from a url
function getDataForHindu(newspaperId, catId , feedUrl){
    console.log("getHinduData NEW & CAT ID - " + newspaperId + ", "+catId+ " feedUrl "+feedUrl);
      
    //create array and use Promise.when to wait till it is filled
    var promisesGetData = []; 
    return Parse.Cloud.httpRequest({
        url: feedUrl
    }).then(function(httpResponse){
        promisesGetData.push(processDataForHindu(httpResponse, newspaperId, catId));
        return Parse.Promise.when(promisesGetData);
          
    }).then(function() {
        console.log("Done getHinduData? ");
        return Parse.Promise.as("Got HinduData");
    });
}
 
function processDataForHindu(httpResponse, newspaperId, catId){
    console.log("processData NEW & CAT ID - " + newspaperId + ", "+catId);
    someXml = httpResponse.text
      
    //using xmlreader.js and sax.js to parse xml into text
    xmlreader.read(someXml, function (err, res){
        if(err) {
            return console.log(err);
        }   
          
        //create a new array and push objects with data in it       
        var listArray = [];
        res.rss.channel.item.each(function (i, item){
            var newsArt = new hindu_data();
            newsArt.set("link", item.link.text());
            newsArt.set("title", item.title.text());
            newsArt.set("pubDate", item.pubDate.text());
            newsArt.set("newspaper_id",newspaperId);
            newsArt.set("cat_id",catId);
             
            listArray.push(newsArt);
        });
 
        //create a new promises array
        var promises = new Array();
        Parse.Object.saveAll(listArray, {                                                                   // 1 API REQUEST
                success: function(objs) {
                    promises.push(objs);
                    console.log("SAVED ALL!");
                },
                error: function(error) { 
                    console.log("ERROR WHILE SAVING - "+error.message);
                }   
            });
        return Parse.Promise.when(promises);        
    });
}
  
Parse.Cloud.beforeSave("hindu_data", function(request, response) {
     
    var linkText = request.object.get("link")
    var titleText = request.object.get("title");
 
    var query = new Parse.Query(hindu_data);
    query.equalTo("title", titleText);
    query.limit(1000);
    query.first({
        success: function(object) {
            // console.log("in query");
            if (object) {
                // console.log("found");
                response.error();
                 
            } else {
                // console.log("not found");
                response.success();
            }
        },
        error: function(error) {
        response.error();
      }
    });
     
     
});
 
 
/////////////////////////////////// toi ////////////////////////////////
 
 
Parse.Cloud.job("get_data_toi_all", function (request, response) {     // 1 API REQUEST
    //news_cat class contains the newspaper_category pairs 
    var newsCatList = Parse.Object.extend("toi_url");
    var query = new Parse.Query(newsCatList);                                                            
    query.find().then(function(list){                                                                       // 1 API REQUEST
        console.log("Successfully retrieved " + list.length);
        //create array and use Promise.when to wait till it is filled
        var promisesGetNP = [];
        for (var i = 0; i < list.length; i++) { 
            promisesGetNP.push(getDataForToi(list[i].get("newspaper_id_column"), list[i].get("category_id_column"), list[i].get("url_column")));
        }
        return Parse.Promise.when(promisesGetNP);
          
    }).then (function(result) {
            console.log("done toi job");
            response.success("Saving completed successfully.");
        },function(error) {
            console.log("toi job not done, error - "+error.message);
            response.error("Uh oh, something went wrong.");
        }
    );
}); 
  
//get httpResponse from a url
function getDataForToi(newspaperId, catId , feedUrl){
    console.log("gettoiData NEW & CAT ID - " + newspaperId + ", "+catId+ " feedUrl "+feedUrl);
      
    //create array and use Promise.when to wait till it is filled
    var promisesGetData = []; 
    return Parse.Cloud.httpRequest({
        url: feedUrl
    }).then(function(httpResponse){
        promisesGetData.push(processDataForToi(httpResponse, newspaperId, catId));
        return Parse.Promise.when(promisesGetData);
          
    }).then(function() {
        console.log("Done getToiData? ");
        return Parse.Promise.as("Got toiData");
    });
}
 
function processDataForToi(httpResponse, newspaperId, catId){
    console.log("processData NEW & CAT ID - " + newspaperId + ", "+catId);
    someXml = httpResponse.text
      
    //using xmlreader.js and sax.js to parse xml into text
    xmlreader.read(someXml, function (err, res){
        if(err) {
            return console.log(err);
        }   
          
        //create a new array and push objects with data in it       
        var listArray = [];
        res.rss.channel.item.each(function (i, item){
            var newsArt = new toi_data();
            newsArt.set("link", item.link.text());
            newsArt.set("title", item.title.text());
            newsArt.set("pubDate", item.pubDate.text());
            newsArt.set("newspaper_id",newspaperId);
            newsArt.set("cat_id",catId);
             
            listArray.push(newsArt);
        });
 
        //create a new promises array
        var promises = new Array();
        Parse.Object.saveAll(listArray, {                                                                   // 1 API REQUEST
                success: function(objs) {
                    promises.push(objs);
                    console.log("SAVED ALL!");
                },
                error: function(error) { 
                    console.log("ERROR WHILE SAVING - "+error.message);
                }   
            });
        return Parse.Promise.when(promises);        
    });
}
  
Parse.Cloud.beforeSave("toi_data", function(request, response) {
     
    var linkText = request.object.get("link")
    var titleText = request.object.get("title");
 
    var query = new Parse.Query(toi_data);
    query.equalTo("title", titleText);
    query.limit(1000);
    query.first({
        success: function(object) {
            // console.log("in query");
            if (object) {
                // console.log("found");
                response.error();
                 
            } else {
                // console.log("not found");
                response.success();
            }
        },
        error: function(error) {
        response.error();
      }
    });
     
     
});
 
/////////////////////////////////// Firspost ////////////////////////////////
 
 
Parse.Cloud.job("get_data_fp_all", function (request, response) {     // 1 API REQUEST
    //news_cat class contains the newspaper_category pairs 
    var newsCatList = Parse.Object.extend("fp_url");
    var query = new Parse.Query(newsCatList);                                                            
    query.find().then(function(list){                                                                       // 1 API REQUEST
        console.log("Successfully retrieved " + list.length);
        //create array and use Promise.when to wait till it is filled
        var promisesGetNP = [];
        for (var i = 0; i < list.length; i++) { 
            promisesGetNP.push(getDataForFp(list[i].get("newspaper_id_column"), list[i].get("category_id_column"), list[i].get("url_column")));
        }
        return Parse.Promise.when(promisesGetNP);
          
    }).then (function(result) {
            console.log("done fp job");
            response.success("Saving completed successfully.");
        },function(error) {
            console.log("fp job not done, error - "+error.message);
            response.error("Uh oh, something went wrong.");
        }
    );
}); 
  
//get httpResponse from a url
function getDataForFp(newspaperId, catId , feedUrl){
    console.log("getfpData NEW & CAT ID - " + newspaperId + ", "+catId+ " feedUrl "+feedUrl);
      
    //create array and use Promise.when to wait till it is filled
    var promisesGetData = []; 
    return Parse.Cloud.httpRequest({
        url: feedUrl
    }).then(function(httpResponse){
        promisesGetData.push(processDataForFp(httpResponse, newspaperId, catId));
        return Parse.Promise.when(promisesGetData);
          
    }).then(function() {
        console.log("Done getFpData? ");
        return Parse.Promise.as("Got fpData");
    });
}
 
function processDataForFp(httpResponse, newspaperId, catId){
    console.log("processData NEW & CAT ID - " + newspaperId + ", "+catId);
    someXml = httpResponse.text
      
    //using xmlreader.js and sax.js to parse xml into text
    xmlreader.read(someXml, function (err, res){
        if(err) {
            return console.log(err);
        }   
          
        //create a new array and push objects with data in it       
        var listArray = [];
        res.rss.channel.item.each(function (i, item){
            var newsArt = new fp_data();
            newsArt.set("link", item.link.text());
            newsArt.set("title", item.title.text());
            newsArt.set("pubDate", item.pubDate.text());
            newsArt.set("newspaper_id",newspaperId);
            newsArt.set("cat_id",catId);
             
            listArray.push(newsArt);
        });
 
        //create a new promises array
        var promises = new Array();
        Parse.Object.saveAll(listArray, {                                                                   // 1 API REQUEST
                success: function(objs) {
                    promises.push(objs);
                    console.log("SAVED ALL!");
                },
                error: function(error) { 
                    console.log("ERROR WHILE SAVING - "+error.message);
                }   
            });
        return Parse.Promise.when(promises);        
    });
}
  
Parse.Cloud.beforeSave("fp_data", function(request, response) {
     
    var linkText = request.object.get("link")
    var titleText = request.object.get("title");
 
    var query = new Parse.Query(fp_data);
    query.equalTo("title", titleText);
    query.limit(1000);
    query.first({
        success: function(object) {
            // console.log("in query");
            if (object) {
                // console.log("found");
                response.error();
                 
            } else {
                // console.log("not found");
                response.success();
            }
        },
        error: function(error) {
        response.error();
      }
    });
     
     
});
 
 
/////////////////////////////////// The Indian Express ////////////////////////////////
 
 
Parse.Cloud.job("get_data_tie_all", function (request, response) {     // 1 API REQUEST
    //news_cat class contains the newspaper_category pairs 
    var newsCatList = Parse.Object.extend("tie_url");
    var query = new Parse.Query(newsCatList);                                                            
    query.find().then(function(list){                                                                       // 1 API REQUEST
        console.log("Successfully retrieved " + list.length);
        //create array and use Promise.when to wait till it is filled
        var promisesGetNP = [];
        for (var i = 0; i < list.length; i++) { 
            promisesGetNP.push(getDataForTie(list[i].get("newspaper_id_column"), list[i].get("category_id_column"), list[i].get("url_column")));
        }
        return Parse.Promise.when(promisesGetNP);
          
    }).then (function(result) {
            console.log("done tie job");
            response.success("Saving completed successfully.");
        },function(error) {
            console.log("tie job not done, error - "+error.message);
            response.error("Uh oh, something went wrong.");
        }
    );
}); 
  
//get httpResponse from a url
function getDataForTie(newspaperId, catId , feedUrl){
    console.log("gettieData NEW & CAT ID - " + newspaperId + ", "+catId+ " feedUrl "+feedUrl);
      
    //create array and use Promise.when to wait till it is filled
    var promisesGetData = []; 
    return Parse.Cloud.httpRequest({
        url: feedUrl
    }).then(function(httpResponse){
        promisesGetData.push(processDataForTie(httpResponse, newspaperId, catId));
        return Parse.Promise.when(promisesGetData);
          
    }).then(function() {
        console.log("Done getTieData? ");
        return Parse.Promise.as("Got tieData");
    });
}
 
function processDataForTie(httpResponse, newspaperId, catId){
    console.log("processData NEW & CAT ID - " + newspaperId + ", "+catId);
    someXml = httpResponse.text
      
    //using xmlreader.js and sax.js to parse xml into text
    xmlreader.read(someXml, function (err, res){
        if(err) {
            return console.log(err);
        }   
          
        //create a new array and push objects with data in it       
        var listArray = [];
        res.rss.channel.item.each(function (i, item){
            var newsArt = new tie_data();
            newsArt.set("link", item.link.text());
            newsArt.set("title", item.title.text());
            newsArt.set("pubDate", item.pubDate.text());
            newsArt.set("newspaper_id",newspaperId);
            newsArt.set("cat_id",catId);
             
            listArray.push(newsArt);
        });
 
        //create a new promises array
        var promises = new Array();
        Parse.Object.saveAll(listArray, {                                                                   // 1 API REQUEST
                success: function(objs) {
                    promises.push(objs);
                    console.log("SAVED ALL!");
                },
                error: function(error) { 
                    console.log("ERROR WHILE SAVING - "+error.message);
                }   
            });
        return Parse.Promise.when(promises);        
    });
}
  
Parse.Cloud.beforeSave("tie_data", function(request, response) {
     
    var linkText = request.object.get("link")
    var titleText = request.object.get("title");
 
    var query = new Parse.Query(tie_data);
    query.equalTo("title", titleText);
    query.limit(1000);
    query.first({
        success: function(object) {
            // console.log("in query");
            if (object) {
                // console.log("found");
                response.error();
                 
            } else {
                // console.log("not found");
                response.success();
            }
        },
        error: function(error) {
        response.error();
      }
    });
     
     
});

/**
 * Created by RajPriyaMAC on 4/5/15.
 */
(function(){'use strict';

    $(document).ready(function(){
        var $datatable;

        var getTableBoilerPlate = function(){
            var table = "<table id = \"odTable\" class=\"table diplay cell-border\" width=\"100%\"\n";
            table += "<thead>\n";
        };

        var controlledClassReset = function(){
            $statusResult.removeClass();
            $statusResult.addClass('form-control');
        };
        var resetForm = function(){
            $statusResult.val('');
            $statusResult.attr('placeholder', 'Status');
            controlledClassReset();
        };

        var emptyAndHide = function($object){
            $object.empty();
            $object.hide();
        };

        var setPageTimeout = function(timeToReload){
            console.log("Inside setpageTimeout");
            //clearTimeout(clearPageTimeout);
            loadPageData();

            if(timeToReload === "")
                clearPageTimeout = setTimeout(setPageTimeout(DEFAULT_RELOAD_TIME), DEFAULT_RELOAD_TIME);
            else
                clearPageTimeout = setTimeout(setPageTimeout(timeToReload), timeToReload);
        };

        var typeAhead = function(searchText, array){
            emptyAndHide($serverData);
            var searchData = "^" + searchText.val();
            $serverData.append('<button type="button" id="close" class"close closed" onclick="$(&quot;#serverData&quot;).hide();$(&quot;#serverData&quot;).empty();">&times;</button>');
            for(var index=0; index < array.length; index++){
                var value = array[index];
                if(value.match(searchData)) $serverData.append("<p>" + array[index] + "</p>");
            }
            //show the type ahead div only when it has some data
            //second condition is to prevent listing all the data when user types backspace
            if(($serverData.children ().length > 1) && ($serverData.children ().length < array.length)){
                $serverData.show();
                //onClick of user selection of the name
                $serverData.find ('p').on ('click', function(){
                    var hostData = $(this).text();
                    $('#hostName').val(hostData);
                    emptyAndHide($serverData);
                });
            }
        };

        //Show Toast Notification
        var displayToast = function(duration, message, position, type){
            $().toastmessagr('showToast',
                {
                    inEffectDration: duration,
                    text: message,
                    position: position,
                    stycky: false,
                    type: type,
                    close: function(){console.log("toast is closed...");}
                });
        };

        var loadPageData = function(){
            console.log("Inside loadPageData");
            if($.fn.dataTable.isDataTable('#odTable')){
                console.log("Data Table existed");
                $datatable.destroy();
            }

            //Make the ajax call and load the table data
            $.getJSON("/iw-cc/monitor/loadData.jsp", {
                format: "json"
            }).done(function(data){
                    if(data === "" || data === "undefined" || data === "null"){
                        var message = "Some error occurred while loading the data.";
                        displayToast(600, message, "middle-center", "notice");
                    }else{
                        var table = getTableBoilerPlate();
                        var tBoday;
                        var tBodayStart = '<tbody>';
                        var tRowAndData = "";
                        var tBodyEnd = '</tbody>';
                        $.each(data, function(i, item){
                            if(item.status === "OD is Down"){
                                tRowAndData += "<tr>\n";
                                tRowAndData += "\t<td class=\"danger change-color\">" + item.hostName + "</td>\n";
                                tRowAndData += "\t<td class=\"danger change-color\">" + item.application + "</td>\n";
                                tRowAndData += "\t<td class=\"danger change-color\">" + item.environment + "</td>\n";
                                tRowAndData += "\t<td class=\"danger change-color\">" + item.type + "</td>\n";
                                tRowAndData += "\t<td class=\"danger change-color\">" + item.status + "</td>\n";
                                tRowAndData += "</tr>\n";
                            }else{
                                tRowAndData += "<tr>\n";
                                tRowAndData += "\t<td class=\"success\">" + item.hostName + "</td>\n";
                                tRowAndData += "\t<td class=\"success\">" + item.application + "</td>\n";
                                tRowAndData += "\t<td class=\"success\">" + item.environment + "</td>\n";
                                tRowAndData += "\t<td class=\"success\">" + item.type + "</td>\n";
                                tRowAndData += "\t<td class=\"success\">" + item.status + "</td>\n";
                                tRowAndData += "</tr>\n";
                            }
                        });
                        table += tBodayStart + tRowAndData + tBodyEnd + "</table>\n";
                        $(".add-margine").append(table);
                    }
                    initializeDataTable();
                }).fail(function(jxhr, textStatus, error){
                var err = textStatus + ", " + error;
                console.log("Request Failed: " + err);
            });
        };

        var initializeDataTable = function(){
            $datatable = $('#odTable').DataTable({
                "order" : [[ OD_STATUS_COLUMN_INDEX]],
                initComplete : function(){
                    var api = this.api();
                    api.columns.indexes.flatten().each(function(i){
                        var column = api.column(i);
                        var select = $('<select><option value=""></option></select>')
                            .appendTo($(column.footer()).empty())
                            .on('change', function(){
                                    var val = $.fn.dataTable.util.escapeRegex(
                                        $(this).val()
                                    );
                                    column.search(val ? '^'+val+'$' : '', true. false )
                                        .draw();
                                });
                    });
                }
            });
        };

        loadPageData();
        /**
         * Local Mothods finished.
         */

        var OD_STATUS_COLUMN_INDEX = 4; //column index for "Open Deploy Status" column
        var DEFAULT_RELOAD_TIME = 1000 * 30; //1800000 //mili seconds
        var clearPageTimeout;

        //Jquery selector to find the div and hold server list for type ahead feature.
        var $serverData = $('#serverData');
        $serverData.hide();

        //Jquery selector to fetch hostname
        var $hostName = $('#hostName');
        //Clear any old data prior to page reload
        $hostName.val('');
        //Set the placeholder text
        $hostName.attr('placeholder', 'Enter Host Name');

        //Jquery selector to fetch the element individual server.
        var $statusResult = $('#statusResult');
        resetForm();
        var hostNameArray = [];

        //Load Type ahead data from the server.
        $.getJSON("/iw-cc/monitor/typeAheadData.jsp", {
            sendData: true,
            format: "json"
        }).done(function(data){
            if(data === "" || data === "undefined" || data === "null"){
                var message = "Type ahead feature is disabled..";
                displayToast(600, message, "middle-center", "notice");
            }else{
                $.each(data, function(i, item){
                    hostNameArray[i] = item.hostname;
                });

                var typingTimer;
                var doneTypingInterval = 5000; //5 seconds

                $hostName.on('keyup', function(event){
                    var code = event.keyCode ? event.keyCode : event.which;
                    if(code === 13){
                        event.preventDefault();
                        return false;
                    }
                    clearTimeout(typingTimer);
                    typingTimer = setTimeout(typeAhead($(this), hostNameArray), doneTypingInterval);
                });

                $hostName.on('keydown', function(){
                    clearTimeout(typingTimer);
                });
            }
        }).fail(function(jxhr, textStatus, error){
            var err = textStatus + ", " + error;
            console.log("Request Failed: " + err);
        });

        $('#hostLookup').on('click', function(){
            event.preventDefault();
            var hostName = $hostName.val();
            controlledClassReset();
            if(hostName !== "" && hostName !== "undefined" && hostName !== "null"){
                $statusResult.val('Checking status .... ');
                $.getJSON("/iw-cc/monitor/checkODStatus.jsp", {
                    hostName : hostName,
                    format : "json"
                }).done(function(data){
                    if(data === "" || data === "undefined" || data === "null"){
                        var message = "Some error occurred while processing the data.";
                        displayToast(600, message, "middle-center", "notice");
                    }else{
                        if(data.status === "OD is Running"){
                            $statusResult.val(data.status);
                            $statusResult.addClass("success");
                        }else{
                            $statusResult.val(data.status);
                            $statusResult.addClass("danger change-color");
                        }
                    }
                }).fail(function(jxhr, textStatus, error){
                    var err = textStatus + ", " + error;
                    console.log("Request Failed: " + err);
                });
            }else{
                displayToast(600, "Please enter a valid hostname.", "middle-center", "notice");
                resetForm();
            }
        });

    });//End of jQuery ready function()

})();

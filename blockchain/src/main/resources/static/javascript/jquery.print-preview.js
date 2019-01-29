/*!
 * jQuery Print Previw Plugin v1.0.1
 *
 * Copyright 2011, Tim Connell
 * Licensed under the GPL Version 2 license
 * http://www.gnu.org/licenses/gpl-2.0.html
 *
 * Date: Wed Jan 25 00:00:00 2012 -000
 */
 
(function($) { 
    var iframeHtml = "";
	// Initialization
	$.fn.printPreview = function() {
		this.each(function() {
			$(this).bind('click', function(e) {
			    e.preventDefault();
			    if (!$('#print-modal').length) {
			        $.printPreview.loadPrintPreview();
			    }
			});
		});
		return this;
	};
    
    // Private functions
    var mask, size, print_modal, print_controls;
    $.printPreview = {
        loadPrintPreview: function() {
            // Declare DOM objects
            print_modal = $('<div id="print-modal"></div>');
            // print_controls = $('<div id="print-modal-controls">' +
            //                         '<a href="#" class="print" title="Print page">Print page</a>' +
            //                         '<a href="#" class="close" title="Close print preview">Close</a>').hide();
            print_controls = $('<div id="print-modal-controls">' +
                '<a href="#" class="close" title="Close">关闭</a>').hide();
            $.printPreview.GetIframeHtml();
            var print_frame = $(iframeHtml);
            iframeHtml = "";
            print_modal
                .hide()
                .append(print_controls)
                .append(print_frame)
                .appendTo('body');

            // The frame lives
            //for (var i=0; i < window.frames.length; i++) {
                //if (window.frames[i].name == "print-frame") {
            var print_frame_ref = $("#print-modal-content").document;
                //    break;
                //}
            //}
            // print_frame_ref.open();
            // print_frame_ref.write('<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">' +
            //     '<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">' +
            //     '<head><title>' + document.title + '</title></head>' +
            //     '<body></body>' +
            //     '</html>');
            // print_frame_ref.close();
            
            //var $iframe_head = $('head link[media*=print], head link[media=all]').clone();
            // 拼接
            //$iframe_body = $(iframeHtml);
            // $iframe_head.each(function() {
            //     $(this).attr('media', 'all');
            // });

            // if (!$.browser.msie && !($.browser.version < 7) ) {
            //     $('head', print_frame_ref).append($iframe_head);
            //     $('body', print_frame_ref).append($iframe_body);
            // }
            // else {
            //     $('body > *:not(#print-modal):not(script)').clone().each(function() {
            //         $('body', print_frame_ref).append($iframe_body);
            //     });
                // $('head link[media*=print], head link[media=all]').each(function() {
                //     $('head', print_frame_ref).append($(this).clone().attr('media', 'all')[0].outerHTML);
                // });
            //}
            
            $('a', print_frame_ref).bind('click.printPreview', function(e) {
                e.preventDefault();
            });
            
            // Introduce print styles
            $('head').append('<style type="text/css">' +
                '@media print {' +
                    '/* -- Print Preview --*/' +
                    '#print-modal-mask,' +
                    '#print-modal {' +
                        'page-break-after: always;' +
                    '}' +
                    '.pagewrap {' +
                        'display: none !important;' +
                    '}' +
                '}' +
                '</style>'
            );
            $.printPreview.loadMask();
            $('body').css({overflowY: 'hidden', height: '100%'});
            starting_position = $(window).height() + $(window).scrollTop();
            var css = {
                    top:         starting_position,
                    height:      '100%',
                    overflowY:   'auto',
                    zIndex:      10000,
                    display:     'block'
                }
            print_modal
                .css(css)
                .animate({ top: $(window).scrollTop()}, 400, 'linear', function() {
                    print_controls.fadeIn('slow').focus();
                });

            // Bind closure
            $('a', print_controls).bind('click', function(e) {
                e.preventDefault();
                if ($(this).hasClass('print')) { window.print(); }
                else {
                    $.printPreview.distroyPrintPreview();
                }
            });
    	},
    	
    	distroyPrintPreview: function() {
    	    print_controls.fadeOut(100);
    	    print_modal.animate({ top: $(window).scrollTop() - $(window).height(), opacity: 1}, 400, 'linear', function(){
    	        print_modal.remove();
    	        $('body').css({overflowY: 'auto', height: 'auto'});
    	    });
    	    mask.fadeOut('slow', function()  {
    			mask.remove();
    		});				

    		$(document).unbind("keydown.printPreview.mask");
    		mask.unbind("click.printPreview.mask");
    		$(window).unbind("resize.printPreview.mask");
	    },
	    
    	/* -- Mask Functions --*/
	    loadMask: function() {
	        size = $.printPreview.sizeUpMask();
            mask = $('<div id="print-modal-mask" />').appendTo($('body'));
    	    mask.css({				
    			position:           'absolute', 
    			top:                0, 
    			left:               0,
    			width:              size[0],
    			height:             size[1],
    			display:            'none',
    			opacity:            0,					 		
    			zIndex:             9999,
    			backgroundColor:    '#000'
    		});
	
    		mask.css({display: 'block'}).fadeTo('400', 0.75);
    		
            $(window).bind("resize..printPreview.mask", function() {
				$.printPreview.updateMaskSize();
			});
			
			mask.bind("click.printPreview.mask", function(e)  {
				$.printPreview.distroyPrintPreview();
			});
			
			$(document).bind("keydown.printPreview.mask", function(e) {
			    if (e.keyCode == 27) {  $.printPreview.distroyPrintPreview(); }
			});
        },
    
        sizeUpMask: function() {
            if ($.browser.msie) {
            	// if there are no scrollbars then use window.height
            	var d = $(document).height(), w = $(window).height();
            	return [
            		window.innerWidth || 						// ie7+
            		document.documentElement.clientWidth || 	// ie6  
            		document.body.clientWidth, 					// ie6 quirks mode
            		d - w < 20 ? w : d
            	];
            } else { return [$(document).width(), $(document).height()]; }
        },
    
        updateMaskSize: function() {
    		var size = $.printPreview.sizeUpMask();
    		mask.css({width: size[0], height: size[1]});
        },

        GetIframeHtml: function (){
            var classname = $("#classSelect").val();
            var year = $("#yearSelect").val();
            var teacherid = $("#teacherid").val();
            //var trList = $("#datadiv table tbody").children("tr");
            //if (trList.length > 0){
                //var stuList = new Array();
                //for(var i = 0;i < trList.length;i++){
                //    stuList.push(trList.eq(i).attr("data-id"));
                //}
            var datas = {
                'className' : classname,
                'year': year,
                'teacherId': teacherid
            };
            $.ajax({
                type:"post",
                url:"../electronicscertificate/certificatelist",
                data: JSON.stringify(datas),
                contentType : 'application/json',
                dataType : 'json',
                async: false,
                success:function (data) {
                    if (data != null && data.length > 0){
                        iframeHtml += '<iframe id="print-modal-content" style="width:96%; height:780px;" frameborder="0" name="print-frame" src="../electronicscertificate/certificate/showPdf" />';
                    }
                }
            });
            //}
        }
    }
})(jQuery);
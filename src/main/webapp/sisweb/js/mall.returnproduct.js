
function CreateFileUp() {

    //var len = $(".jiatupian ul").children("li").length;
    //if (len < 6) {
        //editType = 'add';
        //var id = "li_" + RndNum(5);
        //curLi = id;
        //var fileid = "file_" + id;

        //$("<input />", {
            //type: "file",
            //style: "position:absolute;top:-50px;",
            //id: fileid,
           //function () {
                //var file = $(this)[0].files[0];
    //var fileInput = document.getElementById('sisImage');

        var fileInput = document.getElementById('sisImage')
        //var filein = $("#sisImage");
        var picture = document.getElementById('picture')
        var file = fileInput.files[0];
        var mpImg = new MegaPixImage(file);
        mpImg.render(picture, {maxWidth: 800, maxHeight: 800, quality: 1});
        uploadImage();

            //}
        //}).appendTo("body");

        //setTimeout(function () { $("#" + fileid).click() }, 10);
    //}
    //else {
    //    showNotificatioin();
    //    hideNotification("最多只能上传5张图片！");
    //}
}


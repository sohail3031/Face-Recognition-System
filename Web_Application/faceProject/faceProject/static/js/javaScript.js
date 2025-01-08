//$(document).ready(function(){
//            $('input[type="file"]').change(function(e){
//            var name = $('input[name="name"]').val();
//            $('').attr('src',function(i,e){
//                return $(this).attr('src').replace("default.jpg","hqdefault.jpg");
//                });
//            console.log(e.target.files[0].name)
//            })
//        });
$(document).ready(function(){

    $('input[type="file"]').change(function (input) {
    var name = $('input[name="name"]').val();
    if (input.files && input.files[0]) {
        var reader = new FileReader();

        reader.onload = function (e) {
            $('input[name="image"]')
                    .attr('src', name)
                    .width(150).height(200);
        };

        reader.readAsDataURL(input.files[0]);
        console.log(reader);
        //alert(reader.readAsDataURL(input.files[0]));
    }
})
});
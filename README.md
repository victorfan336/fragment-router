# fragment-router
To resolve Fragments' coupling 

Some code refer to Router(https://github.com/chenenyu/Router), AndRouter(https://github.com/victorfan336/AndRouter), thanks to all of the contributors.

I used it in my project, and some function need to consummate.

If you're interested in it, please fork or star.

# use
 1.annotation fragment like this:  
 
 @FragmentRoute(moduleName = FragmentBuildInfo.APP, value = "Test")  
 
public class TestFragment extends Fragment {

}  

moduleName is your module name, you may use this annotation in different module.  
you must write all you module name in FragmentBuildInfo class !

value is the unique identification !  

 2.initialization  
 
   Initialization fragmentRouter in your main activity like this:  
   
   FragmentRouter.init(R.id.main_content_view);  
   
   R.id.main_content_view is the FrameLayout's id from xml.  
   
 3.use like this:  
 
 public void onClick() {
        new FragmentRouter.FRouterBuilder()<br/> 
                .tag("Test")<br/> 
                .build()<br/>
                .go(getActivity());<br/>
    }

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

 2.use like this:  
 
 public void onClick() {
        new FragmentRouter.FRouterBuilder()
                .tag("Test")
                .build()
                .go(getActivity());
    }

import bg.sofia.uni.fmi.mjt.socialnetwork.SocialNetwork;
import bg.sofia.uni.fmi.mjt.socialnetwork.SocialNetworkImpl;
import bg.sofia.uni.fmi.mjt.socialnetwork.post.Post;
import bg.sofia.uni.fmi.mjt.socialnetwork.post.SocialFeedPost;
import bg.sofia.uni.fmi.mjt.socialnetwork.profile.DefaultUserProfile;
import bg.sofia.uni.fmi.mjt.socialnetwork.profile.Interest;
import bg.sofia.uni.fmi.mjt.socialnetwork.profile.UserProfile;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {
        try {
            UserProfile p = new DefaultUserProfile("Mit");
            UserProfile k = new DefaultUserProfile("Mitt");
            p.addInterest(Interest.BOOKS);
            k.addInterest(Interest.FOOD);
            p.addFriend(k);
            SocialNetwork s = new SocialNetworkImpl();
            s.registerUser(p);
            Post post = new SocialFeedPost(p, "dsadadadw");
            s.post(p, "dsdasdawd");
            System.out.println(s.getReachedUsers(post).toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
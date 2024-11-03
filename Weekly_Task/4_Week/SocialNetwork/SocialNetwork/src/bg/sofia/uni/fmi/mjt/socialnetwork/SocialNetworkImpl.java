package bg.sofia.uni.fmi.mjt.socialnetwork;

import bg.sofia.uni.fmi.mjt.socialnetwork.exception.UserRegistrationException;
import bg.sofia.uni.fmi.mjt.socialnetwork.post.Post;
import bg.sofia.uni.fmi.mjt.socialnetwork.post.SocialFeedPost;
import bg.sofia.uni.fmi.mjt.socialnetwork.profile.UserProfile;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

public class SocialNetworkImpl implements SocialNetwork {
    private final HashSet<UserProfile> userProfiles;
    private final HashSet<Post> posts;

    public SocialNetworkImpl() {
        userProfiles = new HashSet<>();
        posts = new HashSet<>();
    }

    @Override
    public void registerUser(UserProfile userProfile) throws UserRegistrationException {
        if (userProfile == null) {
            throw new IllegalArgumentException("userProfile is null");
        }
        if (isRegistered(userProfile)) {
            throw new UserRegistrationException("User already registered");
        }
        userProfiles.add(userProfile);
    }

    @Override
    public Set<UserProfile> getAllUsers() {
        return Collections.unmodifiableSet(userProfiles);
    }

    private boolean isRegistered(UserProfile userProfile) {
        return userProfiles.contains(userProfile);
    }

    @Override
    public Post post(UserProfile userProfile, String content) throws UserRegistrationException {
        if (userProfile == null || content == null || content.isBlank()) {
            throw new IllegalArgumentException("userProfile or content are null");
        }
        if (!isRegistered(userProfile)) {
            throw new UserRegistrationException("User not registered");
        }
        Post toAdd = new SocialFeedPost(userProfile, content);
        posts.add(toAdd);
        return toAdd;
    }

    @Override
    public Collection<Post> getPosts() {
        return Collections.unmodifiableCollection(posts);
    }

    private void getReachedUsers(UserProfile fromProfile, UserProfile author,
                                 Set<UserProfile> reachedUsers, Set<UserProfile> wentTroughUsers) {
        for (UserProfile friend : fromProfile.getFriends()) {
            if (wentTroughUsers.contains(friend)) {
                continue;
            }
            wentTroughUsers.add(friend);
            getReachedUsers(friend, author, reachedUsers, wentTroughUsers);
            if (friend.haveInterestInCommon(author)) {
                reachedUsers.add(friend);
            }

        }
    }

    @Override
    public Set<UserProfile> getReachedUsers(Post post) {
        if (post == null) {
            throw new IllegalArgumentException("post is null");
        }
        Set<UserProfile> reachedUsers = new HashSet<>();
        getReachedUsers(post.getAuthor(), post.getAuthor(), reachedUsers, new HashSet<>());
        return reachedUsers;
    }

    @Override
    public Set<UserProfile> getMutualFriends(UserProfile userProfile1, UserProfile userProfile2)
            throws UserRegistrationException {
        if (userProfile1 == null || userProfile2 == null) {
            throw new IllegalArgumentException("userProfile1 or userProfile2 are null");
        }
        if (!isRegistered(userProfile1) || !isRegistered(userProfile2)) {
            throw new UserRegistrationException("User not registered");
        }
        HashSet<UserProfile> mutualFriends = new HashSet<>();
        for (UserProfile userProfile : userProfile1.getFriends()) {
            if (!isRegistered(userProfile)) {
                continue;
            }
            if (userProfile2.isFriend(userProfile)) {
                mutualFriends.add(userProfile);
            }
        }
        return mutualFriends;
    }

    @Override
    public SortedSet<UserProfile> getAllProfilesSortedByFriendsCount() {
        return new TreeSet<>(userProfiles);
    }
}

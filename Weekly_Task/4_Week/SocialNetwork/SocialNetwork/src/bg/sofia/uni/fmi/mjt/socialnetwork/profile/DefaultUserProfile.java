package bg.sofia.uni.fmi.mjt.socialnetwork.profile;

import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Objects;

public class DefaultUserProfile implements Comparable<DefaultUserProfile>, UserProfile {
    private String username;
    private final EnumSet<Interest> interests;
    private final HashSet<UserProfile> friends;

    public DefaultUserProfile(String username) {
        this.username = username;
        interests = EnumSet.noneOf(Interest.class);
        friends = new HashSet<>();
    }

    @Override
    public int compareTo(DefaultUserProfile o) {
        return o.friends.size() - this.getFriends().size();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DefaultUserProfile that = (DefaultUserProfile) o;
        return Objects.equals(username, that.username);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username);
    }

    public String getUsername() {
        return username;
    }

    public Collection<Interest> getInterests() {
        return Collections.unmodifiableCollection(interests);
    }

    public boolean haveInterestInCommon(UserProfile userProfile2) {
        if (userProfile2 == null) {
            throw new IllegalArgumentException("userProfile is null");
        }
        for (Interest interest : this.getInterests()) {
            if (userProfile2.getInterests().contains(interest)) {
                return true;
            }
        }
        return false;
    }

    public boolean addInterest(Interest interest) {
        if (interest == null) {
            throw new IllegalArgumentException("interest is null");
        }
        if (interests.contains(interest)) {
            return false;
        } else {
            interests.add(interest);
            return true;
        }
    }

    public boolean removeInterest(Interest interest) {
        if (interest == null) {
            throw new IllegalArgumentException("interest is null");
        }
        return interests.remove(interest);
    }

    public Collection<UserProfile> getFriends() {
        return Collections.unmodifiableCollection(friends);
    }

    public boolean addFriend(UserProfile userProfile) {
        if (userProfile == null) {
            throw new IllegalArgumentException("userProfile is null");
        }
        if (this.equals(userProfile)) {
            throw new IllegalArgumentException("Cannot add oneself as a friend");
        }
        if (isFriend(userProfile)) {
            return false;
        } else {
            friends.add(userProfile);
            userProfile.addFriend(this);
            return true;
        }
    }

    public boolean unfriend(UserProfile userProfile) {
        if (userProfile == null) {
            throw new IllegalArgumentException("userProfile is null");
        }
        if (!isFriend(userProfile)) {
            return false;
        } else {
            friends.remove(userProfile);
            userProfile.unfriend(this);
            return true;
        }
    }

    public boolean isFriend(UserProfile userProfile) {
        if (userProfile == null) {
            throw new IllegalArgumentException("UserProfile is null");
        }
        return friends.contains(userProfile);
    }
}

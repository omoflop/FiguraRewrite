package net.blancworks.figura.customization.avatar;

import java.util.HashMap;
import java.util.UUID;

/***
 * The FiguraAvatarManager is at the heart of Figura itself- It handles storing avatars, loading them from byte data,
 * and serving avatars based on UUID.
 * 
 * The UUID used to access the avatar should be the UUID of the owner, not the UUID of the avatar itself (as seen on
 * the backends)
 */
public class FiguraAvatarManager {

    private static HashMap<UUID, FiguraAvatar> avatarMap = new HashMap<>();

    public static void init() {
        
    }

    /***
     * Serves an avatar from the manager, if one exists.
     * @param id the ID of the owner of the avatar.
     * @return Null if no avatar is found, otherwise, the avatar owned by the entity with this UUID.
     */
    public static FiguraAvatar getAvatar(UUID id) {
        return avatarMap.get(id);
    }

    /***
     * Generates a new avatar, or gets an existing one.
     * @param id The ID of the avatar to check for.
     * @return The current avatar at the given UUID, or a new one if none was found.
     */
    public static FiguraAvatar generateNewAvatar(UUID id){
        FiguraAvatar currAvatar = avatarMap.get(id);
        
        if(currAvatar == null){
            currAvatar = new FiguraAvatar();
            avatarMap.put(id, currAvatar);
        }
        
        return currAvatar;
    }
    
}

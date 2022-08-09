package main.controller;

import lombok.AllArgsConstructor;
import main.api.request.CommentRequest;
import main.api.request.ModeratorDecisionRequest;
import main.api.request.ProfileRequest;
import main.api.request.SettingsRequest;
import main.api.response.CalendarResponse;
import main.api.response.InitResponse;
import main.api.response.SettingsResponse;
import main.api.response.TagResponse;
import main.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@AllArgsConstructor
public class GeneralController {
    @Autowired
    private SettingsService settingsService;
    @Autowired
    private InitResponse initResponse;
    @Autowired
    private TagService tagService;
    @Autowired
    private CalendarService calendarService;

    @Autowired
    private ImageService imageService;

    @Autowired
    private CommentService commentService;

    @Autowired
    private PostService postService;

    @Autowired
    private ProfileService profileService;


    @GetMapping("/init")
    public InitResponse init() {
        return initResponse;
    }

    @GetMapping("/settings")
    public ResponseEntity<SettingsResponse> settings() {
        return ResponseEntity.ok(settingsService.getGlobalSettings());
    }

    @GetMapping("/tag")
    public ResponseEntity<Map<String, List<TagResponse>>> getTags(@RequestParam(defaultValue = "") String query) {
        return ResponseEntity.ok(tagService.getTasks(query));
    }

    @GetMapping("/calendar")
    public ResponseEntity<CalendarResponse> getCalendar(@RequestParam(defaultValue = "0") int year) {
        return ResponseEntity.ok(calendarService.getCalendar(year));
    }

    @PostMapping(value = "/image", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    @PreAuthorize("hasAuthority('user:write')")
    public ResponseEntity<?> postImage(@RequestParam MultipartFile image) throws IOException {
        return imageService.postImage(image);

    }

    @PostMapping("/comment")
    @PreAuthorize("hasAuthority('user:write')")
    public ResponseEntity<?> postComment(@RequestBody CommentRequest commentRequest, Principal principal) {
        return commentService.postComment(commentRequest, principal.getName());
    }

    @PostMapping("/moderation")
    @PreAuthorize("hasAuthority('user:moderate')")
    public ResponseEntity<Map<String, Object>> postModeration(@RequestBody ModeratorDecisionRequest moderatorDecisionRequest, Principal principal) {
        return ResponseEntity.ok(postService.editPostStatus(moderatorDecisionRequest, principal.getName()));
    }

    @PutMapping("/settings")
    @PreAuthorize("hasAuthority('user:moderate')")
    public ResponseEntity<?> updateSettings(@RequestBody SettingsRequest settingsRequest) {
        settingsService.setGlobalSettings(settingsRequest);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping(value = "/profile/my", consumes = {MediaType.APPLICATION_JSON_VALUE})
    @PreAuthorize("hasAuthority('user:write')")
    public ResponseEntity<Map<String, Object>> updateMyProfile(@RequestBody ProfileRequest profileRequest, Principal principal) throws IOException {
        return ResponseEntity.ok(profileService.postMyProfile(profileRequest.getPhoto(),
                profileRequest.getEmail(),
                profileRequest.getName(),
                profileRequest.getPassword(),
                profileRequest.getRemovePhoto(),
                principal.getName()));
    }

    @PostMapping(value = "/profile/my", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    @PreAuthorize("hasAuthority('user:write')")
    public ResponseEntity<Map<String, Object>> updateMyProfileWithPhoto(@RequestParam("photo") MultipartFile photo,
                                                      @RequestParam("removePhoto") int removePhoto,
                                                      @RequestParam("name") String name,
                                                      @RequestParam("email") String email,
                                                      @RequestParam(name = "password", required = false) String password,
                                                      Principal principal) throws IOException {
        return ResponseEntity.ok(profileService.postMyProfile(photo, email, name, password, removePhoto, principal.getName()));
    }
}

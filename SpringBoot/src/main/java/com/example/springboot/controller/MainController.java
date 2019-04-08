package com.example.springboot.controller;

import com.example.springboot.domain.Message;
import com.example.springboot.domain.User;
import com.example.springboot.repositories.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collector;
import java.util.stream.Collectors;

@Controller
public class MainController {


    @Autowired
    private MessageRepository messageRepository;

    @Value("${upload.path}")
    private String uploadPath;

    @GetMapping("/")
    public String greeting(Map<String, Object> model) {
        return "greeting";
    }

    @GetMapping("/main")
    public String main(@RequestParam(required = false) String filter,
                       Model model,
                       @PageableDefault(sort = {"id"}, direction = Sort.Direction.DESC)Pageable pageable)
    {
        Page<Message> page;

        if (filter != null && !filter.isEmpty()) {
            page = messageRepository.findByTag(filter, pageable);
        }
        else {
            page = messageRepository.findAll(pageable);
        }

        model.addAttribute("page", page);
        model.addAttribute("url", "/main");
        model.addAttribute("filter", filter);

        return "main";
    }

    @PostMapping("/main")
    public String add(
                @AuthenticationPrincipal User user,
                @Valid Message message,
                BindingResult bindingResult,
                Model model,
                @RequestParam("file") MultipartFile file,
                @PageableDefault(sort = {"id"}, direction = Sort.Direction.DESC) Pageable pageble
    ) throws IOException {

        Page<Message> page = messageRepository.findByAuthor(user,pageble);
        message.setAuthor(user);
        Set<Message> messagesSet = user.getMessages();

        if(bindingResult.hasErrors()){
            Map<String, String> errorsMap = ControllerUtils.getErrors(bindingResult);
            model.mergeAttributes(errorsMap);
            model.addAttribute("message", message);
        } else {
            saveFile(message, file);

            model.addAttribute("message", null);
            messageRepository.save(message);

        }
//        Iterable<Message> messages = messageRepository.findAll();
        model.addAttribute("messages", messagesSet);
        model.addAttribute("url", "/user-messages/" + user.getId());
        model.addAttribute("page", page);

        return "redirect://localhost:8082/main";
    }

    private void saveFile(@Valid Message message, @RequestParam("file") MultipartFile file) throws IOException {
        if (file != null && !file.getOriginalFilename().isEmpty()) {
            File uploadDir = new File(uploadPath);

            if (!uploadDir.exists()) {
                uploadDir.mkdir();
            }

            String uuidFile = UUID.randomUUID().toString();
            String resultFilename = uuidFile + "." + file.getOriginalFilename();
            file.transferTo(new File(uploadPath + "/" + resultFilename));
            message.setFilename(resultFilename);
        }
    }


    @PostMapping("filter")
    public String findByTag(@RequestParam String filter,Map<String, Object> model){
        Iterable<Message> messages;

        return "main";
    }

    @GetMapping("/user-messages/{user}")
    public String userMessages(
            @AuthenticationPrincipal User currentUser,
            @PathVariable User user,
            @PageableDefault(sort = {"id"}, direction = Sort.Direction.DESC) Pageable pageble,
            @RequestParam(required = false) Message message,
            Model model
    ){
        Page<Message> page = messageRepository.findByAuthor(user, pageble);

        Set<Message> messages = user.getMessages();

        model.addAttribute("subscriptionsCount", user.getSubscriptions().size());
        model.addAttribute("subscribersCount", user.getSubscribers().size());
        model.addAttribute("userChannel", user);
        model.addAttribute("isSubscriber", user.getSubscribers().contains(currentUser));
        model.addAttribute("messages", messages);
        model.addAttribute("message", message);
        model.addAttribute("url", "/user-messages/" + user.getId());
        model.addAttribute("page", page);
        model.addAttribute("isCurrentUser", currentUser.equals(user));

        return "userMessages";
    }

    @PostMapping("/user-messages/{user}")
    public String updateMessage(
            @AuthenticationPrincipal User currentUser,
            @PathVariable Long user,
            @RequestParam("id") Message message,
            @RequestParam("text") String text,
            @RequestParam("tag") String tag,
            @RequestParam("file") MultipartFile file,
            @PageableDefault(sort = {"id"}, direction = Sort.Direction.DESC) Pageable pageble,
            Model model
    ) throws IOException {
        Page<Message> page = messageRepository.findByAuthor(currentUser, pageble);

        Set<Message> messages = currentUser.getMessages();

        if(message.getAuthor().equals(currentUser)){
            if(!StringUtils.isEmpty(text)){
                message.setText(text);
            }
            if(!StringUtils.isEmpty(tag)){
                message.setTag(tag);
            }
            saveFile(message, file);

            model.addAttribute("url", "/user-messages/" + currentUser.getId());
            model.addAttribute("page", page);
            messageRepository.save(message);

        }

        return "redirect:/user-messages/" + user;
    }




}
package com.vlim.puebla;

class ChatBubble {
    private String content;
    private boolean myMessage;
    private String side;

    public ChatBubble(String content, boolean myMessage) {
        this.content = content;
        this.myMessage = myMessage;
    }

    public ChatBubble(String conversacion, boolean myMessage, String side) {
        this.content = conversacion;
        this.myMessage = myMessage;
        this.side = side;
    }

    public String getContent() {
        return content;
    }

    public boolean myMessage() {
        return myMessage;
    }

    public String side() {
        return side;
    }
}

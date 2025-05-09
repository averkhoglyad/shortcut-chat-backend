package io.averkhoglyad.shortcut.notification.data

data class ChatEventWithMembers(
    val event: ChatLifecycleEvent,
    val members: Collection<UserRef>,
)
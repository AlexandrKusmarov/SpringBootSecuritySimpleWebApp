<#import "parts/common.ftl" as c />

<@c.page>
<h3>${userChannel.username}</h3>
    <#if !isCurrentUser>
        <#if isSubscriber>
            <a href="/user/unsubscribe/${userChannel.id}"><button class="btn btn-info">Unsubscribe</button></a>
        <#else>
            <a href="/user/subscribe/${userChannel.id}"><button class="btn btn-info">Subscribe</button></a>
        </#if>
    </#if>
<div class="container my-3">
    <div class="row">
        <div class="col">
            <div class="card">
                <div class="card-body">
                    <h3 class="card-title">Subscriptions</h3>
                    <div class="card-text">
                        <a href="/user/subscriptions/${userChannel.id}/list">${subscriptionsCount}</a>
                    </div>
                </div>
            </div>
        </div>
        <div class="col">
            <div class="card">
                <div class="card-body">
                    <h3 class="card-title">Subscribers</h3>
                    <div class="card-text">
                        <a href="/user/subscribers/${userChannel.id}/list">${subscribersCount}</a>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>
    <#if isCurrentUser>
        <#include "parts/messageEdit.ftl" />
    </#if>
    <#include "parts/messageList.ftl" />
</@c.page>
$ ->
  $('#new-conference').click (e) ->
    e.preventDefault()
    target = $(e.target)
    container = target.closest '.container'
    date = container.find('input[name=date]').val()
    time = container.find('input[name=time]').val()
    contacts = $.map container.find('.addpart'), (e) ->
      name: $(e).find('input[name=name]').val()
      email: $(e).find('input[name=email]').val()
      phone: $(e).find('input[name=phone]').val()
      initiator: $(e).hasClass('initiator')

    data =
      date: "#{date} #{time}"
      title: container.find('input[name=title]').val()
      agenda: container.find('textarea[name=agenda]').val()
      contacts: contacts

    target.attr 'value', 'Sending...'

    $.ajax
      type: 'POST'
      contentType: 'application/json'
      url: jsRoutes.controllers.Application.newConference().url
      data: JSON.stringify data
      success: (data) -> window.location = data
      error: -> alert 'failed'

  $('.console-item button[data-action="start-conference"]').click (e) ->
    id = $(e.target).closest('.console-item').attr 'data-id'
    $.ajax
      type: 'GET'
      url: jsRoutes.controllers.Telekom.startConference(id).url
      success: -> alert 'done'
      error: -> alert 'failed'

  $('#next-participant').click (e) ->
    $.ajax
      type: 'GET'
      url: jsRoutes.controllers.Application.participantForm().url
      success: (data) ->
        $(e.target).closest('.buttonadd').before data

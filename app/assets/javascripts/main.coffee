$ ->
  $('#new-conference').click (e) ->
    form = $(e.target).closest '.form'
    data =
      date: form.find('input[name=date]').val()
      agenda: form.find('textarea[name=agenda]').val()
      contacts: form.find('textarea[name=contacts]').val()

    $.ajax
      type: 'POST'
      contentType: 'application/json'
      url: jsRoutes.controllers.Application.newConference().url
      data: JSON.stringify data
      success: -> alert 'done'
      error: -> alert 'failed'

  $('.console-item button[data-action="start-conference"]').click (e) ->
    id = $(e.target).closest('.console-item').attr 'data-id'
    $.ajax
      type: 'GET'
      url: jsRoutes.controllers.Telekom.startConference(id).url
      success: -> alert 'done'
      error: -> alert 'failed'

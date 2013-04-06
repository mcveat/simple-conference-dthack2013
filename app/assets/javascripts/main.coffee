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
      success: ->
        console.log 'works'

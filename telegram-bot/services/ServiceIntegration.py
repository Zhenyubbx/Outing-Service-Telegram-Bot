from telegram.ext import ConversationHandler, MessageHandler, Filters
import constants
from services.AttractionService import AttractionService
from services.FNBService import FNBService
from services.BarsAndClubsService import BARS_CLUBService
import telegram
from telegram import ReplyKeyboardMarkup

class ServiceIntegration:
    def __init__(self, dp, keyboard):
        keyboard.append([constants.ATTRACTIONS, constants.F00D_BEVERAGE, constants.BARS_CLUBS])
        service_integration_conversation_handler = ConversationHandler(
                entry_points=[
                    MessageHandler(Filters.regex(constants.F00D_BEVERAGE), lambda update, context : self.trigger_fnb_service(update, context)),
                    MessageHandler(Filters.regex(constants.BARS_CLUBS), lambda update, context : self.trigger_bars_clubs_service(update, context)),
                    MessageHandler(Filters.regex(constants.ATTRACTIONS), lambda update, context : self.trigger_attractions_service(update, context))
                    ],
                states = {
                    constants.USING_SERVICE : [BARS_CLUBService(keyboard).get_conversation_handler(), 
                                               FNBService(keyboard).get_conversation_handler(),
                                               AttractionService(keyboard).get_conversation_handler()
                                               ],
                    constants.CHOOSING_SERVICE : [MessageHandler(Filters.regex(constants.F00D_BEVERAGE), lambda update, context : self.trigger_fnb_service(update, context)),
                                                MessageHandler(Filters.regex(constants.BARS_CLUBS), lambda update, context : self.trigger_bars_clubs_service(update, context)),
                                                MessageHandler(Filters.regex(constants.ATTRACTIONS), lambda update, context : self.trigger_attractions_service(update, context))]
                },
                fallbacks=[
                    MessageHandler(Filters.all, lambda update, context : self.fallback(update, context))
                    ]
            )
        dp.add_handler(service_integration_conversation_handler)
        self.service_keyboard = keyboard    

    def trigger_attractions_service(self, update, context):
        keyboard = AttractionService.get_service_functions()
        reply_markup = ReplyKeyboardMarkup(keyboard,
                                       one_time_keyboard=True,
                                       resize_keyboard=True)
        message = "Please select an option in the keyboard!"
        update.message.reply_text(message, reply_markup=reply_markup, parse_mode=telegram.ParseMode.HTML)
        return constants.USING_SERVICE
    
    def trigger_fnb_service(self, update, context):
        keyboard = FNBService.get_service_functions()
        reply_markup = ReplyKeyboardMarkup(keyboard,
                                       one_time_keyboard=True,
                                       resize_keyboard=True)
        message = "Please select an option in the keyboard!"
        update.message.reply_text(message, reply_markup=reply_markup, parse_mode=telegram.ParseMode.HTML)
        return constants.USING_SERVICE
    
    def trigger_bars_clubs_service(self, update, context):
        keyboard = BARS_CLUBService.get_service_functions()
        reply_markup = ReplyKeyboardMarkup(keyboard,
                                       one_time_keyboard=True,
                                       resize_keyboard=True)
        message = "Please select an option in the keyboard!"
        update.message.reply_text(message, reply_markup=reply_markup, parse_mode=telegram.ParseMode.HTML)
        return constants.USING_SERVICE

    def fallback(self, update, context):
        user_id = update.message.from_user.id
        if 'history' in context.user_data:
            if user_id in context.user_data['history']:
                message, state, currReplyMarkup, previous_context = context.user_data['history'][user_id][-1]
                message = "Please select one of the options in the keyboard!"
                update.message.reply_text(message, reply_markup = currReplyMarkup, parse_mode=telegram.ParseMode.HTML)
                return state
            else:
                message = "No history of user found!"
                update.message.reply_text(message)
        else:
            message = "History not instantiated in context"
            update.message.reply_text(message)

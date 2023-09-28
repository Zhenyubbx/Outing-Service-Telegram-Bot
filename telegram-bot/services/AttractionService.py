from telegram.ext import ConversationHandler, MessageHandler, Filters
import constants
import requests
import os
from telegram import ReplyKeyboardMarkup
from bs4 import BeautifulSoup
import telegram

def remove_html_tags(html_string):
    soup = BeautifulSoup(html_string, 'html.parser')
    return soup.get_text()    

#States
WAITING_LOCATION, WAITING_KEYWORD, WAITING_NEAREST_ATTRACTIONS_COMMAND, WAITING_ATTRACTIONS_BY_KEYWORD_COMMAND, SEARCHING_ATTRACTION_DETAILS = range(5)

class AttractionService:
    def __init__(self, service_keyboard):
        ATTRACTION_service_conversation_handler = ConversationHandler(
                entry_points=[
                    MessageHandler(Filters.regex(constants.SEARCH_ATTRACTION_DETAILS), lambda update, context : self.get_ATTRACTION_name_for_search(update, context)),
                    MessageHandler(Filters.regex(constants.GET_NEAREST_ATTRACTIONS), lambda update, context : self.get_curr_location(update, context)),
                    MessageHandler(Filters.regex(constants.SEARCH_ATTRACTIONS), lambda update, context : self.get_keyword(update, context)),
                    MessageHandler(Filters.regex(constants.GET_ATTRACTION_REVIEWS), lambda update, context : self.get_ATTRACTION_reviews(update, context)),
                    MessageHandler(Filters.regex(constants.BACK), lambda update, context : self.back(update, context))
                    ],
                states = {
                    SEARCHING_ATTRACTION_DETAILS : [MessageHandler(Filters.regex(constants.BACK), lambda update, context : self.back(update, context)),
                                                    MessageHandler(Filters.all, lambda update, context : self.search_ATTRACTION_details(update, context))],
                    WAITING_LOCATION : [MessageHandler(Filters.regex(constants.BACK), lambda update, context : self.back(update, context)),
                                        MessageHandler(Filters.all, lambda update, context : self.get_first_page_of_nearest_ATTRACTIONS(update, context))],
                    WAITING_NEAREST_ATTRACTIONS_COMMAND : [MessageHandler(Filters.regex(constants.BACK), lambda update, context : self.back(update, context)),
                                                           MessageHandler(Filters.regex(constants.NEXT_PAGE), lambda update, context : self.get_next_page_of_nearest_ATTRACTIONS(update, context)),
                                        MessageHandler(Filters.regex(constants.PREV_PAGE), lambda update, context : self.get_prev_page_of_nearest_ATTRACTIONS(update, context)),
                                        MessageHandler(Filters.regex(constants.EXIT), lambda update, context : self.exit(update, context, service_keyboard)),
                                        MessageHandler(Filters.regex(constants.SEARCH_ATTRACTION_DETAILS), lambda update, context : self.get_ATTRACTION_name_for_search(update, context))],
                    WAITING_KEYWORD : [MessageHandler(Filters.regex(constants.BACK), lambda update, context : self.back(update, context)),
                                       MessageHandler(Filters.all, lambda update, context : self.get_first_page_of_attractions_by_keyword(update, context))],
                    WAITING_ATTRACTIONS_BY_KEYWORD_COMMAND : [MessageHandler(Filters.regex(constants.BACK), lambda update, context : self.back(update, context)),
                                                              MessageHandler(Filters.regex(constants.NEXT_PAGE), lambda update, context : self.get_next_page_of_attractions_by_keyword(update, context)),
                                        MessageHandler(Filters.regex(constants.PREV_PAGE), lambda update, context : self.get_prev_page_of_attractions_by_keyword(update, context)),
                                        MessageHandler(Filters.regex(constants.EXIT), lambda update, context : self.exit(update, context, service_keyboard)),
                                        MessageHandler(Filters.regex(constants.SEARCH_ATTRACTION_DETAILS), lambda update, context : self.get_ATTRACTION_name_for_search(update, context))]
                    },
                fallbacks=[
                    MessageHandler(Filters.all, lambda update, context : self.fallback(update, context))
                    ],
                map_to_parent={
                    ConversationHandler.END : constants.CHOOSING_SERVICE
                },
                allow_reentry=True
            )
        
        self.convsation_handler = ATTRACTION_service_conversation_handler

    def get_service_functions():
        keyboard=[]
        keyboard.append([constants.GET_NEAREST_ATTRACTIONS, constants.SEARCH_ATTRACTIONS])
        return keyboard

    def get_conversation_handler(self):
        return self.convsation_handler
        
    def get_curr_location(self, update, context):
        message = "What is your current location?"
        update.message.reply_text(message)
        self.log_state(update, context, message, WAITING_LOCATION, currReplyMarkup=None)
        return WAITING_LOCATION
    
    def get_nearest_ATTRACTIONS(self, location, update, context):
        url = f"{os.getenv('BASE_URL')}/{os.getenv('API_VERSION')}/tourism-service/attractions?pageSize={context.user_data['pageSize']}&pageNo={context.user_data['pageNo']}&currLocation={context.user_data['location']}"
        response = requests.get(url).json()

        context.user_data['totalPage'] = response["data"]["totalPage"]

        if context.user_data['pageNo'] < 0:
            context.user_data['pageNo'] = 0
        elif context.user_data['pageNo'] > context.user_data['totalPage']:
            context.user_data['pageNo'] = context.user_data['totalPage']
        
        
        if response["code"] == 200:
            message = ""
            for idx in range(response["data"]["pageSize"]):
                message += (f"<b>{idx + 1 + (context.user_data['pageNo'] * context.user_data['pageSize'])}. {response['data']['data'][idx]['stbDataDTO']['name']}</b>\n" +
                            f"<b>Location:</b> {response['data']['data'][idx]['stbDataDTO']['address']['streetName']}\n" + 
                            f"<b>Duration:</b> {response['data']['data'][idx]['duration']['text']}\n" +
                            f"<b>Description:</b> {response['data']['data'][idx]['stbDataDTO']['description']}\n\n")
                context.user_data['pageATTRACTIONS'][response['data']['data'][idx]['stbDataDTO']['name'].strip()] = response['data']['data'][idx]['stbDataDTO']['uuid']
            message += "\n *All durations based on public transport mode"
            keyboard=[[constants.EXIT, constants.BACK],[],[constants.SEARCH_ATTRACTION_DETAILS]]
            if context.user_data['pageNo'] < context.user_data['totalPage']:
                keyboard[1].append(constants.NEXT_PAGE)
            if context.user_data['pageNo'] > 0:
                keyboard[1].append(constants.PREV_PAGE)
            reply_markup = ReplyKeyboardMarkup(keyboard,
                                       one_time_keyboard=True,
                                       resize_keyboard=True)
            update.message.reply_text(message, reply_markup=reply_markup, parse_mode=telegram.ParseMode.HTML)
            self.log_state(update, context, message, WAITING_NEAREST_ATTRACTIONS_COMMAND, reply_markup)
            return WAITING_NEAREST_ATTRACTIONS_COMMAND
        else:
            update.message.reply_text(response['message'])
            return ConversationHandler.END

    def get_first_page_of_nearest_ATTRACTIONS(self, update, context):
        context.user_data['pageNo'] = 0
        context.user_data['pageSize'] = 5
        context.user_data['pageATTRACTIONS'] = {}
        context.user_data['location'] = update.message.text
        self.get_nearest_ATTRACTIONS(context.user_data['location'], update, context)
        return WAITING_NEAREST_ATTRACTIONS_COMMAND

    def get_next_page_of_nearest_ATTRACTIONS(self, update, context):
        context.user_data['pageNo'] = context.user_data['pageNo'] + 1
        context.user_data['pageATTRACTIONS'] = {}
        self.get_nearest_ATTRACTIONS(context.user_data['location'], update, context)
        return WAITING_NEAREST_ATTRACTIONS_COMMAND
        
    def get_prev_page_of_nearest_ATTRACTIONS(self, update, context):
        context.user_data['pageNo'] = context.user_data['pageNo'] -1
        context.user_data['pageATTRACTIONS'] = {}
        self.get_nearest_ATTRACTIONS(context.user_data['location'], update, context)
        return WAITING_NEAREST_ATTRACTIONS_COMMAND

    def exit(self, update, context, service_keyboard):
        message = "Exited conversation for Attraction Service"
        reply_markup = ReplyKeyboardMarkup(service_keyboard,
                                       one_time_keyboard=True,
                                       resize_keyboard=True)
        update.message.reply_text(message, reply_markup = reply_markup)
        return ConversationHandler.END

    def get_ATTRACTION_name_for_search(self, update, context):
        keyboard=[[constants.BACK]]
        for row in range(0, len(context.user_data['pageATTRACTIONS']), 2):
            first_name = list(context.user_data['pageATTRACTIONS'].keys())[row]
            second_name = None
            if row + 1 < len(context.user_data['pageATTRACTIONS']):
                second_name = list(context.user_data['pageATTRACTIONS'].keys())[row + 1]
            if second_name != None:
                keyboard.append([first_name, second_name])
            else:
                keyboard.append([first_name])
        reply_markup = ReplyKeyboardMarkup(keyboard,
                                       one_time_keyboard=True,
                                       resize_keyboard=True)
        message = "Please select ATTRACTION"
        update.message.reply_text(text = message, reply_markup=reply_markup)
        self.log_state(update, context, message, SEARCHING_ATTRACTION_DETAILS, reply_markup)
        return SEARCHING_ATTRACTION_DETAILS
    
    def search_ATTRACTION_details(self, update, context):
        print("ATTRACTIONS: " + str(list(context.user_data['pageATTRACTIONS'].keys())))
        name = update.message.text
        if name in context.user_data['pageATTRACTIONS']:
            uuid = context.user_data['pageATTRACTIONS'][name]

            url = f"{os.getenv('BASE_URL')}/{os.getenv('API_VERSION')}/tourism-service/attractions/uuid/{uuid}"
            response = requests.get(url).json()

            context.user_data['ATTRACTION'] = response

            message = (f"<b>{response['data']['name']}</b>\n\n" + 
                       f"<b>Location:</b> {response['data']['address']['streetName']}\n\n" +
                       f"<b>Description:</b> {response['data']['description']}\n\n" + 
                       f"<b>Body:</b> {remove_html_tags(response['data']['body'])}\n\n")
            
            message += "<b>Business Hours:</b> \n"
            for businessHour in response['data']['businessHour']:
                message += f"""{businessHour["day"]}: {businessHour["openTime"]} - {businessHour["closeTime"]}\n"""
            
            keyboard = [[constants.BACK, constants.GET_ATTRACTION_REVIEWS]]
            reply_markup = ReplyKeyboardMarkup(keyboard,
                                       one_time_keyboard=True,
                                       resize_keyboard=True)
            update.message.reply_text(text = message, parse_mode=telegram.ParseMode.HTML, reply_markup=reply_markup)
            self.log_state(update, context, message, SEARCHING_ATTRACTION_DETAILS, reply_markup)
            # return SEARCHING_ATTRACTION_DETAILS
        else:
            message = f"Unable to retrieve ATTRACTION details for {name}"
            update.message.reply_text(text = message)
        return ConversationHandler.END

    def get_ATTRACTION_reviews(self, update, context):
        message = "<b>Reviews:</b> \n"
        if len(context.user_data['ATTRACTION']['data']['reviews']) > 0:
            count = 1
            for review in context.user_data['ATTRACTION']['data']['reviews']:
                message += (f"<b>{count}Author:</b> {review['authorName']}\n" +
                            f"<b>Rating:</b> {review['rating']}\n" + 
                            f"<b>Review:</b> {review['text']}\n\n")
                count += 1
        else:
            message = f"No reviews found for {context.user_data['ATTRACTION']['data']['name']}!"
        keyboard = [[constants.BACK]]
        reply_markup = ReplyKeyboardMarkup(keyboard,
                                    one_time_keyboard=True,
                                    resize_keyboard=True)
        update.message.reply_text(text = message, parse_mode=telegram.ParseMode.HTML, reply_markup = reply_markup)
        self.log_state(update, context, message, SEARCHING_ATTRACTION_DETAILS, reply_markup)
        
    def get_keyword(self, update, context):
        message = "Please type your keywords"
        update.message.reply_text(text=message)
        self.log_state(update, context, message, WAITING_KEYWORD, currReplyMarkup=None)
        return WAITING_KEYWORD

    def search_attractions_by_keyword(self, update, context):
        url = f"{os.getenv('BASE_URL')}/{os.getenv('API_VERSION')}/tourism-service/attractions/keyword/{context.user_data['keyword']}?pageSize={context.user_data['pageSize']}&pageNo={context.user_data['pageNo']}"
        response = requests.get(url).json()

        context.user_data['totalPage'] = response["data"]["totalPage"]

        if context.user_data['pageNo'] < 0:
            context.user_data['pageNo'] = 0
        elif context.user_data['pageNo'] > context.user_data['totalPage']:
            context.user_data['pageNo'] = context.user_data['totalPage']
        
        
        if response["code"] == 200:
            message = ""
            for idx in range(response["data"]["pageSize"]):
                message += (f"<b>{idx + 1 + (context.user_data['pageNo'] * context.user_data['pageSize'])}. {response['data']['data'][idx]['name']}</b>\n" +
                            f"<b>Location:</b> {response['data']['data'][idx]['address']['streetName']}\n" +
                            f"<b>Description:</b> {remove_html_tags(response['data']['data'][idx]['description'])}\n\n")
                context.user_data['pageATTRACTIONS'][response['data']['data'][idx]['name'].strip()] = response['data']['data'][idx]['uuid']
            message += "\n *All durations based on public transport mode"
            keyboard=[[constants.EXIT, constants.BACK],[],[constants.SEARCH_ATTRACTION_DETAILS]]

            if context.user_data['pageNo'] < context.user_data['totalPage']:
                keyboard[1].append(constants.NEXT_PAGE)
            if context.user_data['pageNo'] > 0:
                keyboard[1].append(constants.PREV_PAGE)
            reply_markup = ReplyKeyboardMarkup(keyboard,
                                       one_time_keyboard=True,
                                       resize_keyboard=True)
            update.message.reply_text(message, reply_markup=reply_markup, parse_mode=telegram.ParseMode.HTML)
            self.log_state(update, context, message, WAITING_ATTRACTIONS_BY_KEYWORD_COMMAND, reply_markup)
            return WAITING_ATTRACTIONS_BY_KEYWORD_COMMAND
        else:
            update.message.reply_text(response['message'])
            return ConversationHandler.END
    def get_first_page_of_attractions_by_keyword(self, update, context):
        context.user_data['pageNo'] = 0
        context.user_data['pageSize'] = 5
        context.user_data['pageATTRACTIONS'] = {}
        context.user_data['keyword'] = update.message.text
        self.search_attractions_by_keyword(update, context)
        print("ATTRACTIONS: " + str(list(context.user_data['pageATTRACTIONS'].keys())))
        return WAITING_ATTRACTIONS_BY_KEYWORD_COMMAND

    def get_next_page_of_attractions_by_keyword(self, update, context):
        context.user_data['pageNo'] = context.user_data['pageNo'] + 1
        context.user_data['pageATTRACTIONS'] = {}
        self.search_attractions_by_keyword(update, context)
        print("ATTRACTIONS: " + str(list(context.user_data['pageATTRACTIONS'].keys())))
        return WAITING_ATTRACTIONS_BY_KEYWORD_COMMAND
        
    def get_prev_page_of_attractions_by_keyword(self, update, context):
        context.user_data['pageNo'] = context.user_data['pageNo'] -1
        context.user_data['pageATTRACTIONS'] = {}
        self.search_attractions_by_keyword(update, context)
        print("ATTRACTIONS: " + str(list(context.user_data['pageATTRACTIONS'].keys())))
        return WAITING_ATTRACTIONS_BY_KEYWORD_COMMAND

    def back(self, update, context):
        user_id = update.message.from_user.id
        print("back: " + str(len(context.user_data['history'][user_id])))
        if user_id in context.user_data['history'] and len(context.user_data['history'][user_id]) > 1:
            # Get the previous context data
            message, state, currReplyMarkup, previous_context = context.user_data['history'][user_id][-2]

            # # Restore the previous conversation state and context
            for key in previous_context.keys():
                if key != "history":
                    context.user_data[key] = previous_context[key]
            keys_to_remove = [key for key in context.user_data if key not in previous_context]
            for key in keys_to_remove:
                if key != 'history':
                    del context.user_data[key]

            if currReplyMarkup != None:
                update.message.reply_text(message, reply_markup = currReplyMarkup, parse_mode=telegram.ParseMode.HTML)
            else:
                update.message.reply_text(message, parse_mode=telegram.ParseMode.HTML)

            # Delete latest user history
            del context.user_data['history'][user_id][-1]

            print("current state" + str(state))
            return state
        
        elif user_id in context.user_data['history'] and len(context.user_data['history'][user_id]) == 1:
            message = "No backward navigation allowed"
            update.message.reply_text(message)

            # Get the current context data
            message, state, currReplyMarkup, previous_context = context.user_data['history'][user_id][0]

            update.message.reply_text(message, reply_markup = currReplyMarkup, parse_mode=telegram.ParseMode.HTML)

            return state
        else:
            message = "Error: No user history found"
            update.message.reply_text(message)
            return ConversationHandler.END
    
    def log_state(self, update, context, message, state, currReplyMarkup):
        user_id = update.message.from_user.id
        if 'history' not in context.user_data:
            context.user_data['history'] = {}
        if user_id not in context.user_data['history']:
            context.user_data['history'][user_id] = []

        # Store the current message and context data in the user history stack
        curr_context = {}
        for key in context.user_data:
            if key != "history":
                curr_context[key] = context.user_data[key]
        context.user_data['history'][user_id].append((message, state, currReplyMarkup, curr_context))
        print("logging state in Attraction Service: " + str(state))
    
    def fallback(self, update, context):
        user_id = update.message.from_user.id
        if 'history' in context.user_data:
            if user_id in context.user_data['history']:
                message, state, currReplyMarkup, previous_context = context.user_data['history'][user_id][-1]
                message = "Please select one of the options in the keyboard"
                update.message.reply_text(message, reply_markup = currReplyMarkup, parse_mode=telegram.ParseMode.HTML)
                print("logging state in Attraction Service in fallback: " + str(state))
                return state
            else:
                message = "No history of user found!"
                update.message.reply_text(message)
        else:
            message = "History not instantiated in context"
            update.message.reply_text(message)
